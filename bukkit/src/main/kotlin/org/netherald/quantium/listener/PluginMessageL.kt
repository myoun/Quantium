package org.netherald.quantium.listener

import com.google.common.io.ByteStreams
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import org.netherald.quantium.Channels
import org.netherald.quantium.data.ConnectionType
import org.netherald.quantium.data.PlayerData
import org.netherald.quantium.data.QuantiumConfig
import org.netherald.quantium.util.PluginMessagePlayerUtil

class PluginMessageL : PluginMessageListener {
    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        @Suppress("UnstableApiUsage")
        val data = ByteStreams.newDataInput(message)

        when (data.readUTF()) {
            Channels.SubChannels.getMiniGamePlayerCountResponse -> {
                val id = data.readLong()
                PluginMessagePlayerUtil.callbackData[id] = data.readInt()
            }

            Channels.SubChannels.Bungee.lobbyConnection -> {
                if (PlayerData.connectionType[player] != ConnectionType.LOBBY) {
                    PlayerData.connectionType[player] = ConnectionType.LOBBY
                    player.teleport(QuantiumConfig.lobbyLocation)
                }
            }

            Channels.SubChannels.Bungee.miniGameConnection -> {
                PlayerData.connectionType[player] = ConnectionType.MINIGAME
                TODO()
            }
        }
    }
}