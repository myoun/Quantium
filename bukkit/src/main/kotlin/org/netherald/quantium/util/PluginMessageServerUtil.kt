package org.netherald.quantium.util

import com.google.common.io.ByteStreams
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.netherald.quantium.Channels
import org.netherald.quantium.Quantium
import org.netherald.quantium.data.MiniGameData
import java.util.*

class PluginMessageServerUtil(val serverName : String) : ServerUtil {

    companion object {
        var instance : PluginMessageServerUtil? = null
        val queuedMessage = ArrayList<ByteArray>()
    }

    private val blocked = true
    override val isBlocked: Boolean get() = blocked

    init {
        Bukkit.getScheduler().runTaskTimer(Quantium.plugin, { _ ->
            requestMiniGames()
        }, 0, 200)
    }

    var games: Collection<String> = MiniGameData.miniGames.map { it.key }

    override val miniGames: Collection<String> get() = games

    private fun requestMiniGames() {
        @Suppress("UnstableApiUsage")
        val out = ByteStreams.newDataOutput()
        out.writeUTF(Channels.SubChannels.GET_MINI_GAMES)

        val playerArray = Bukkit.getOnlinePlayers().stream().toArray()
        if (playerArray.isNotEmpty()) {
            (playerArray[0] as Player).sendPluginMessage(Quantium.plugin, Channels.MAIN_CHANNEL, out.toByteArray())
        }
    }

    override fun setBlockServer(value: Boolean) {

        @Suppress("UnstableApiUsage")
        val out = ByteStreams.newDataOutput()
        out.writeBoolean(value)
        sendPluginMessage(out.toByteArray())

    }

    private fun sendPluginMessage(data : ByteArray) {
        val playerArray = Bukkit.getOnlinePlayers().stream().toArray()
        if (playerArray.isNotEmpty()) {
            (playerArray[0] as Player).sendPluginMessage(Quantium.plugin, Channels.MAIN_CHANNEL, data)
        } else {
            queuedMessage.add(data)
        }
    }
}