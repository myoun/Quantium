package org.netherald.quantium.util

import com.google.common.io.ByteStreams
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.Channels
import kotlin.random.Random

class PluginMessagePlayerUtil : PlayerUtil() {

    lateinit var plugin : JavaPlugin

    override fun sendToLobby(player: Player) {
        @Suppress("UnstableApiUsage")
        val out = ByteStreams.newDataOutput()
        out.writeUTF(Channels.SubChannels.lobby)
        player.sendPluginMessage(plugin, Channels.mainChannel, out.toByteArray())
    }

    override fun sendToMiniGame(player: Player, miniGame : String) {
        @Suppress("UnstableApiUsage")
        val out = ByteStreams.newDataOutput()
        out.writeUTF(Channels.SubChannels.game)
        out.writeUTF(miniGame)
        player.sendPluginMessage(plugin, Channels.mainChannel, out.toByteArray())
    }

    override fun sendToServer(player: Player, serverName : String) {
        @Suppress("UnstableApiUsage")
        val out = ByteStreams.newDataOutput()
        out.writeUTF("Connect")
        out.writeUTF(serverName)
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray())
    }

    companion object {
        val callbackData = HashMap<Long, Int>()
    }

    override fun getMiniGamePlayerCount(miniGameName: String) : Int {
        @Suppress("UnstableApiUsage")
        val out = ByteStreams.newDataOutput()
        val id = Random.nextLong()

        out.writeUTF(Channels.SubChannels.getMiniGamePlayerCount)
        out.writeLong(id)
        out.writeUTF(miniGameName)
        (Bukkit.getServer().onlinePlayers as List<Player>)[0]
            .sendPluginMessage(plugin, Channels.mainChannel, out.toByteArray())

        val time = System.currentTimeMillis()
        while (callbackData[id] == null) {
            if (System.currentTimeMillis() <= time + 15000) {
                throw RuntimeException("getMiniGamePlayerCount time out")
            }
        }

        val value = callbackData[id]!!
        callbackData.remove(id)
        return value
    }
}