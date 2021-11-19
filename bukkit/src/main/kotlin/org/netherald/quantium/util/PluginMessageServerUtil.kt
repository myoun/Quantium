package org.netherald.quantium.util

import com.google.common.io.ByteStreams
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.netherald.quantium.Channels
import org.netherald.quantium.Quantium
import org.netherald.quantium.data.MiniGameData
import java.util.*

class PluginMessageServerUtil : ServerUtil {

    companion object {
        var instance : PluginMessageServerUtil? = null
        val queuedMessage = ArrayList<ByteArray>()
    }

    override val miniGames: Collection<String>
        get() = games

    override val instances: Collection<UUID> = TODO()

    override fun getInstances(game: String): Collection<UUID>? = instances0[game]

    private val blocked = true
    override val isBlocked: Boolean get() = blocked

    init {
        Bukkit.getScheduler().runTaskTimer(Quantium.plugin, { _ ->
            requestMiniGames()
            requestInstances()
        }, 0, 200)
    }

    var games: Collection<String> = MiniGameData.miniGames.map { it.key }
    var instances0 : Map<String, Collection<UUID>> = instancesInit()

    private fun instancesInit() : Map<String, Collection<UUID>> {
        val out = HashMap<String, Collection<UUID>>()
        MiniGameData.miniGames.forEach { (name, miniGame) ->
            val instances = miniGame.instances.map { it.uuid }
            out[name] = instances
        }
        return out
    }

    private fun requestMiniGames() {
        @Suppress("UnstableApiUsage")
        val out = ByteStreams.newDataOutput()
        out.writeUTF(Channels.SubChannels.GET_MINI_GAMES)
        sendPluginMessage0(out.toByteArray())
    }

    private fun requestInstances() {
        games.forEach {
            @Suppress("UnstableApiUsage")
            val out = ByteStreams.newDataOutput()
            out.writeUTF(Channels.SubChannels.GET_INSTANCES)
            out.writeUTF(it)
            sendPluginMessage0(out.toByteArray())
        }
    }

    override fun setBlockServer(value: Boolean) {

        @Suppress("UnstableApiUsage")
        val out = ByteStreams.newDataOutput()
        out.writeBoolean(value)
        sendPluginMessage(out.toByteArray())

    }

    fun sendPluginMessage(data : ByteArray) {
        if (!sendPluginMessage0(data)) {
            queuedMessage.add(data)
        }
    }

    private fun sendPluginMessage0(data : ByteArray) : Boolean {
        val playerArray = Bukkit.getOnlinePlayers().stream().toArray()
        if (playerArray.isNotEmpty()) {
            (playerArray[0] as Player).sendPluginMessage(Quantium.plugin, Channels.MAIN_CHANNEL, data)
        }
        return playerArray.isNotEmpty()
    }
}