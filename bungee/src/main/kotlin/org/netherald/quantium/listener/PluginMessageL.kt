package org.netherald.quantium.listener

import com.google.common.io.ByteStreams
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import org.netherald.quantium.Channels
import org.netherald.quantium.data.MiniGameData
import org.netherald.quantium.data.PlayerData
import org.netherald.quantium.data.ServerData
import org.netherald.quantium.data.servers
import org.netherald.quantium.event.MiniGameConnectingEvent
import org.netherald.quantium.util.PlayerConnectionUtil

class PluginMessageL : Listener {
    @EventHandler
    fun on(event : PluginMessageEvent) {
        if (event.tag == Channels.mainChannel && event.receiver is ProxiedPlayer) {
            val player = event.receiver as ProxiedPlayer
            @Suppress("UnstableApiUsage")
            val data = ByteStreams.newDataInput(event.data)
            when (data.readUTF()) {
                Channels.SubChannels.Bukkit.lobby -> {
                    PlayerConnectionUtil.connect(
                        player,
                        ServerData.lobby,
                        PlayerConnectionUtil.SelectionAlgorithm.PLAYER_COUNT_LOWER
                    )
                }

                Channels.SubChannels.Bukkit.game -> {
                    val miniGame = data.readUTF()
                    MiniGameData.minigames[miniGame]?.let {
                        if (!ProxyServer.getInstance().pluginManager.callEvent(
                            MiniGameConnectingEvent(player, it)).isCancelled
                        ) {
                            PlayerData.playerPlayingMap[player.uniqueId] = it
                            PlayerConnectionUtil.connect(
                                player,
                                it.servers,
                                PlayerConnectionUtil.SelectionAlgorithm.PLAYER_COUNT_LOWER
                            )
                        }
                    }
                }

                Channels.SubChannels.getMiniGamePlayerCount -> {

                    @Suppress("UnstableApiUsage")
                    val out = ByteStreams.newDataOutput()

                    out.writeUTF(Channels.SubChannels.getMiniGamePlayerCountResponse)
                    out.writeLong(data.readLong())

                    MiniGameData.minigames[data.readUTF()]?.let {
                        out.writeInt(it.players.size)
                    } ?: run {
                        out.writeInt(-1)
                    }

                    (event.receiver as ProxiedPlayer).server.sendData(Channels.mainChannel, out.toByteArray())

                }
            }
        }
    }
}