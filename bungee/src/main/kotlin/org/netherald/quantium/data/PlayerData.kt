package org.netherald.quantium.data

import net.md_5.bungee.api.connection.ProxiedPlayer
import org.netherald.quantium.MiniGameInstance
import java.lang.NullPointerException
import java.util.*
import kotlin.collections.HashMap

object PlayerData {
    // im making graph data library
    // so..... data going to be changing to beautiful style
    val playerPlayingMap = HashMap<UUID, MiniGameInstance>()

    fun setPlayingMiniGame(player: ProxiedPlayer, value : MiniGameInstance) {
        val mutable = player.playingMiniGame?.players as MutableCollection<UUID>
        mutable.remove(player.uniqueId)
        playerPlayingMap[player.uniqueId] = value
        (value.players as MutableCollection<UUID>).add(player.uniqueId)
    }
}

val ProxiedPlayer.playingMiniGame : MiniGameInstance? get() = PlayerData.playerPlayingMap[uniqueId]