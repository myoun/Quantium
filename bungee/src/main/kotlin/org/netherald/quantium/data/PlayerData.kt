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
    get() = PlayerData.playerPlayingMap[this.uniqueId]
    set(value) {
        value?.let {
            PlayerData.playerPlayingMap[this.uniqueId]?.players?.remove(this.uniqueId)
            PlayerData.playerPlayingMap[this.uniqueId] = it
            PlayerData.playerPlayingMap[this.uniqueId]!!.players.add(this.uniqueId)
        } ?: run {
            throw NullPointerException()
        }
    }