package org.netherald.quantium.listener

import com.google.common.io.ByteStreams
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.connection.Server
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.plugin.Event
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import org.netherald.quantium.Channels
import org.netherald.quantium.MiniGameInfo
import org.netherald.quantium.MiniGameInstance
import org.netherald.quantium.data.MiniGameData
import org.netherald.quantium.data.isBlocked
import org.netherald.quantium.debug
import org.netherald.quantium.event.InstanceAddedEvent
import org.netherald.quantium.event.InstanceDeletedEvent
import org.netherald.quantium.event.MiniGameConnectingEvent
import org.netherald.quantium.exception.AlreadyPlayingException
import org.netherald.quantium.util.PlayerConnectionUtil
import org.netherald.quantium.exception.NotFoundMiniGameException
import java.util.*
import kotlin.collections.HashMap

class PluginMessageL : Listener {
    companion object {
        val miniGameTypeMap = HashMap<UUID, MiniGameInfo>()
    }
    @EventHandler
    fun on(event : PluginMessageEvent) {
        if (event.tag == Channels.MAIN_CHANNEL && event.receiver is ProxiedPlayer) {
            val player = event.receiver as ProxiedPlayer
            val server = event.sender as Server
            val callEvent = fun (event : Event) = ProxyServer.getInstance().pluginManager.callEvent(event)
            @Suppress("UnstableApiUsage")
            val data = ByteStreams.newDataInput(event.data)
            val subChannel = data.readUTF()
            debug("plugin-message subChannel : $subChannel")
            when (subChannel) {

                Channels.SubChannels.Bukkit.ADDED_INSTANCE -> {
                    MiniGameData.miniGames[data.readUTF()]?.let {
                        val uuid = UUID.fromString(data.readUTF())
                        miniGameTypeMap[uuid] = it
                        val instance = MiniGameInstance(
                            uuid,
                            server.info,
                            it
                        )
                        MiniGameData.instances[uuid] = instance
                        (it.instances as MutableCollection<MiniGameInstance>) += instance
                        callEvent(InstanceAddedEvent(instance))
                    }
                }

                Channels.SubChannels.Bukkit.DELETED_INSTANCE -> {
                    val uuid = UUID.fromString(data.readUTF())
                    debug(uuid.toString())
                    miniGameTypeMap[uuid]?.let {
                        val instance = MiniGameData.instances[uuid]!!
                        (it.instances as MutableCollection<MiniGameInstance>) -= instance
                        miniGameTypeMap -= uuid
                        callEvent(InstanceDeletedEvent(instance))
                    }
                }

                Channels.SubChannels.Bukkit.LOBBY -> {
                    PlayerConnectionUtil.connectToLobby(player)
                }

                Channels.SubChannels.Bukkit.GAME -> {
                    val miniGame = data.readUTF()
                    debug(miniGame)
                    MiniGameData.miniGames[miniGame]?.let {
                        if (!ProxyServer.getInstance().pluginManager.callEvent(
                            MiniGameConnectingEvent(player, it)
                            ).isCancelled
                        ) {
                            try {
                                PlayerConnectionUtil.connectToGame(player, it)
                            } catch (e : AlreadyPlayingException) {}
                        }
                    } ?: throw NotFoundMiniGameException(miniGame)
                }

                Channels.SubChannels.Bukkit.SET_BLOCK -> {
                    val value = data.readBoolean()
                    debug("value : $value")
                    server.info.isBlocked = value
                }

                Channels.SubChannels.GET_MINI_GAME_PLAYER_COUNT -> {

                    @Suppress("UnstableApiUsage")
                    val out = ByteStreams.newDataOutput()

                    out.writeUTF(Channels.SubChannels.GET_MINI_GAME_PLAYER_COUNT_RESPONSE)
                    out.writeLong(data.readLong())

                    MiniGameData.miniGames[data.readUTF()]?.let {
                        out.writeInt(it.players.size)
                    } ?: run {
                        out.writeInt(-1)
                    }

                    (event.receiver as ProxiedPlayer).server.sendData(Channels.MAIN_CHANNEL, out.toByteArray())

                }

                Channels.SubChannels.Bukkit.STARTED_INSTANCE -> {
                    val uuid = UUID.fromString(data.readUTF())
                    val instance = MiniGameData.instances[uuid]!!
                    instance.isStarted = true
                }

                Channels.SubChannels.Bukkit.STOPPED_INSTANCE -> {
                    val uuid = UUID.fromString(data.readUTF())
                    val instance = MiniGameData.instances[uuid]!!
                    instance.isStopped = true
                }

                Channels.SubChannels.Bukkit.ADDED_REJOIN_DATA -> {
                    val uuid = UUID.fromString(data.readUTF())
                    val instance = MiniGameData.instances[uuid]!!
                    TODO()
                }

                Channels.SubChannels.Bukkit.REMOVED_REJOIN_DATA -> {
                    val uuid = UUID.fromString(data.readUTF())
                    val instance = MiniGameData.instances[uuid]!!
                    TODO()
                }

            }
        }
    }
}