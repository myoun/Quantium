package org.netherald.quantium.listener

import com.google.common.io.ByteStreams
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import org.netherald.quantium.Channels
import org.netherald.quantium.data.ConnectionType
import org.netherald.quantium.data.PlayerData
import org.netherald.quantium.data.QuantiumConfig
import org.netherald.quantium.util.PluginMessagePlayerUtil
import org.netherald.quantium.util.PluginMessageServerUtil

class PluginMessageL : PluginMessageListener {
    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        @Suppress("UnstableApiUsage")
        val data = ByteStreams.newDataInput(message)

        when (data.readUTF()) {
            Channels.SubChannels.GET_MINI_GAME_PLAYER_COUNT_RESPONSE -> {
                val id = data.readLong()
                PluginMessagePlayerUtil.callbackData[id] = data.readInt()
            }

            Channels.SubChannels.Bungee.LOBBY_CONNECTION -> {
                if (PlayerData.connectionType[player] != ConnectionType.LOBBY) {
                    PlayerData.connectionType[player] = ConnectionType.LOBBY
                    player.teleport(QuantiumConfig.lobbyLocation)
                }
            }

            Channels.SubChannels.Bungee.MINI_GAME_CONNECTION -> {
                PlayerData.connectionType[player] = ConnectionType.MINIGAME
                TODO()
            }

            Channels.SubChannels.GET_MINI_GAMES_RESPONSE -> {
                val list = ArrayList<String>(data.readInt())
                for (i in 0 until list.size) {
                    list.add(data.readUTF())
                }
                PluginMessageServerUtil.instance?.let {
                    it.games = list
                }
            }
        }
    }
}