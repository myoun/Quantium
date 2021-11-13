package org.netherald.quantium.listener

import com.google.common.io.ByteStreams
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import org.netherald.quantium.Channels
import org.netherald.quantium.data.MiniGameData
import org.netherald.quantium.event.MiniGameConnectingEvent
import org.netherald.quantium.util.PlayerConnectionUtil

class PluginMessageL : Listener {
    @EventHandler
    fun on(event : PluginMessageEvent) {
        if (event.tag == Channels.MAIN_CHANNEL && event.receiver is ProxiedPlayer) {
            val player = event.receiver as ProxiedPlayer
            @Suppress("UnstableApiUsage")
            val data = ByteStreams.newDataInput(event.data)
            when (data.readUTF()) {
                Channels.SubChannels.Bukkit.LOBBY -> {
                    PlayerConnectionUtil.connectToLobby(player)
                }

                Channels.SubChannels.Bukkit.GAME -> {
                    val miniGame = data.readUTF()
                    MiniGameData.minigames[miniGame]?.let {
                        if (!ProxyServer.getInstance().pluginManager.callEvent(
                            MiniGameConnectingEvent(player, it)).isCancelled
                        ) {
                            PlayerConnectionUtil.connectToGame(player, it)
                        }
                    }
                }

                Channels.SubChannels.GET_MINI_GAME_PLAYER_COUNT -> {

                    @Suppress("UnstableApiUsage")
                    val out = ByteStreams.newDataOutput()

                    out.writeUTF(Channels.SubChannels.GET_MINI_GAME_PLAYER_COUNT_RESPONSE)
                    out.writeLong(data.readLong())

                    MiniGameData.minigames[data.readUTF()]?.let {
                        out.writeInt(it.players.size)
                    } ?: run {
                        out.writeInt(-1)
                    }

                    (event.receiver as ProxiedPlayer).server.sendData(Channels.MAIN_CHANNEL, out.toByteArray())

                }
            }
        }
    }
}