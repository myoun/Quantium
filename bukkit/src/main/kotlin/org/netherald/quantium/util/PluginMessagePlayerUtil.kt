package org.netherald.quantium.util

import com.google.common.io.ByteStreams
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.Channels
import org.netherald.quantium.Quantium
import kotlin.random.Random

class PluginMessagePlayerUtil : PlayerUtil {

    private val plugin : JavaPlugin get() = Quantium.plugin

    override fun sendToLobby(player: Player) {
        @Suppress("UnstableApiUsage")
        val out = ByteStreams.newDataOutput()
        out.writeUTF(Channels.SubChannels.Bukkit.LOBBY)
        player.sendPluginMessage(plugin, Channels.MAIN_CHANNEL, out.toByteArray())
    }

    override fun sendToMiniGame(player: Player, miniGame : String) {
        @Suppress("UnstableApiUsage")
        val out = ByteStreams.newDataOutput()
        out.writeUTF(Channels.SubChannels.Bukkit.GAME)
        out.writeUTF(miniGame)
        player.sendPluginMessage(plugin, Channels.MAIN_CHANNEL, out.toByteArray())
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
}