package org.netherald.quantium.util

import com.google.common.io.ByteStreams
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.netherald.quantium.Channels
import org.netherald.quantium.Quantium
import java.util.*

class PluginMessageServerUtil(val serverName : String) : ServerUtil {

    companion object {
        val queuedMessage = ArrayList<ByteArray>()
    }

    private val blocked = true
    override val isBlocked: Boolean get() = blocked

    override fun setBlockServer(value: Boolean) {

        @Suppress("UnstableApiUsage")
        val out = ByteStreams.newDataOutput()
        out.writeUTF(Channels.SubChannels.Bukkit.game)
        out.writeUTF(serverName)
        val playerArray = Bukkit.getOnlinePlayers().stream().toArray()
        if (playerArray.isEmpty()) {
            (playerArray[0] as Player).sendPluginMessage(Quantium.plugin, Channels.mainChannel, out.toByteArray())
        } else {
            queuedMessage.add(out.toByteArray())
        }

    }
}