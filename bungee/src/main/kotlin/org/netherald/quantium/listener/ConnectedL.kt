package org.netherald.quantium.listener

import com.google.common.io.ByteStreams
import net.md_5.bungee.api.event.ServerConnectedEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import org.netherald.quantium.Channels
import org.netherald.quantium.data.playingMiniGame

class ConnectedL : Listener {
    @EventHandler
    fun on(event : ServerConnectedEvent) {
        @Suppress("UnstableApiUsage") val out = ByteStreams.newDataOutput()
        event.player?.playingMiniGame?.let {
            out.writeUTF(Channels.SubChannels.Bungee.MINI_GAME_CONNECTION)
            out.writeUTF(it.uuid.toString())
        } ?: run {
            out.writeUTF(Channels.SubChannels.Bungee.LOBBY_CONNECTION)
        }
        event.server.sendData(Channels.MAIN_CHANNEL, out.toByteArray())
    }
}