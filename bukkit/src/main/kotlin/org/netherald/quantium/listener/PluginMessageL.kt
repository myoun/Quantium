package org.netherald.quantium.listener

import com.google.common.io.ByteStreams
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import org.netherald.quantium.Channels
import org.netherald.quantium.data.ConnectionType
import org.netherald.quantium.data.MiniGameData
import org.netherald.quantium.data.PlayerData
import org.netherald.quantium.data.QuantiumConfig
import org.netherald.quantium.util.PluginMessagePlayerUtil
import org.netherald.quantium.util.PluginMessageServerUtil
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class PluginMessageL : PluginMessageListener {
    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != Channels.MAIN_CHANNEL) return
        @Suppress("UnstableApiUsage")
        val data = ByteStreams.newDataInput(message)

        when (data.readUTF()) {
            Channels.SubChannels.GET_MINI_GAME_PLAYER_COUNT_RESPONSE -> {
                val id = data.readLong()
                PluginMessagePlayerUtil.callbackData[id] = data.readInt()
            }

            Channels.SubChannels.Bungee.LOBBY_CONNECTION -> {
                PlayerData.connectionType[player] = ConnectionType.LOBBY
                player.teleport(QuantiumConfig.lobbyLocation)
            }

            Channels.SubChannels.Bungee.MINI_GAME_CONNECTION -> {
                PlayerData.connectionType[player] = ConnectionType.MINIGAME
                val instance = MiniGameData.instances[UUID.fromString(data.readUTF())]!!
                instance.addPlayer(player)
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

            Channels.SubChannels.GET_INSTANCES_RESPONSE -> {
                val miniGame = data.readUTF()
                val instances = HashSet<UUID>()
                for (i in 0..data.readInt()) {
                    instances.add(UUID(data.readLong(), data.readLong()))
                }
                PluginMessageServerUtil.instance?.let {
                    (it.instances0 as MutableMap<String, Collection<UUID>>)[miniGame] = instances
                    it.instances.addAll(instances)
                }
            }
        }
    }
}