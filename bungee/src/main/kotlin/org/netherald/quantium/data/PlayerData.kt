package org.netherald.quantium.data

import net.md_5.bungee.api.connection.ProxiedPlayer
import org.netherald.quantium.MiniGameInfo
import java.lang.NullPointerException
import java.util.*
import kotlin.collections.HashMap

object PlayerData {
    // im making graph data library
    // so..... data going to be changing to beautiful style
    val playerPlayingMap = HashMap<UUID, MiniGameInfo>()
}

var ProxiedPlayer.playingMiniGame : MiniGameInfo?
    get() = PlayerData.playerPlayingMap[uniqueId]
    set(value) {
        value?.let {
            val mutable =  PlayerData.playerPlayingMap[uniqueId]?.players as MutableCollection<UUID>
            mutable.remove(uniqueId)
            PlayerData.playerPlayingMap[uniqueId] = it
            mutable.add(uniqueId)
        } ?: run {
            throw NullPointerException()
        }
    }