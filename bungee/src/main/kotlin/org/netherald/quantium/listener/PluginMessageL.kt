package org.netherald.quantium.listener

import com.google.common.io.ByteStreams
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import org.netherald.quantium.Channels
import org.netherald.quantium.data.MiniGameData
import org.netherald.quantium.data.ServerData
import org.netherald.quantium.event.MiniGameConnectingEvent
import org.netherald.quantium.util.PlayerConnectionUtil

class PluginMessageL : Listener {
    @EventHandler
    fun on(event : PluginMessageEvent) {
        if (event.tag == Channels.mainChannel && event.receiver is ProxiedPlayer) {
            @Suppress("UnstableApiUsage")
            val data = ByteStreams.newDataInput(event.data)
            when (data.readUTF()) {
                Channels.SubChannels.lobby -> {
                    PlayerConnectionUtil.connect(
                        event.receiver as ProxiedPlayer,
                        ServerData.lobby,
                        PlayerConnectionUtil.SelectionAlgorithm.PLAYER_COUNT_LOWER
                    )
                }

                Channels.SubChannels.game -> {
                    val miniGame = data.readUTF()
                    MiniGameData.minigames[miniGame]?.let {
                        if (!ProxyServer.getInstance().pluginManager.callEvent(
                            MiniGameConnectingEvent(
                                event.receiver as ProxiedPlayer, it
                            )).isCancelled
                        ) {
                            MiniGameData.minigames[miniGame]?.queue?.add(event.receiver as ProxiedPlayer)
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