package org.netherald.quantium.util

import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.Channels
import org.netherald.quantium.Quantium
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random

class PluginMessagePlayerUtil : PlayerUtil {

    private val plugin : JavaPlugin get() = Quantium.plugin

    private val newDataOutput : ByteArrayDataOutput get() =
        @Suppress("UnstableApiUsage") ByteStreams.newDataOutput()

    override fun sendToLobby(player: Player) {
        val out = newDataOutput
        out.writeUTF(Channels.SubChannels.Bukkit.LOBBY)
        player.sendPluginMessage(plugin, Channels.MAIN_CHANNEL, out.toByteArray())
    }

    override fun sendToMiniGame(player: Player, miniGame : String) {
        val out = newDataOutput
        out.writeUTF(Channels.SubChannels.Bukkit.MINI_GAME)
        out.writeUTF(miniGame)
        player.sendPluginMessage(plugin, Channels.MAIN_CHANNEL, out.toByteArray())
    }

    override fun sendToInstance(player: Player, instance: UUID) {
        val out = newDataOutput
        out.writeUTF(Channels.SubChannels.Bukkit.INSTANCE)
        out.writeUTF(instance.toString())
        player.sendPluginMessage(plugin, Channels.MAIN_CHANNEL, out.toByteArray())
    }

    override fun sendToServer(player: Player, serverName : String) {
        val out = newDataOutput
        out.writeUTF("Connect")
        out.writeUTF(serverName)
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray())
    }

    companion object {
        val callbackData = HashMap<Long, Int>()
    }
}