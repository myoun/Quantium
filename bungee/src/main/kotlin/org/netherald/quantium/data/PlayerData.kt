package org.netherald.quantium.data

import net.md_5.bungee.api.connection.ProxiedPlayer
import org.netherald.quantium.AlreadyInQueueException
import org.netherald.quantium.MiniGameInfo
import java.lang.NullPointerException
import java.util.*
import kotlin.collections.HashMap

object PlayerData {
    // im making graph data library
    // so..... data going to be changing to beautiful style
    val playerPlayingMap = HashMap<UUID, MiniGameInfo>()
    val playersQueue = HashMap<ProxiedPlayer, MiniGameInfo>()

    fun leaveQueue(player: ProxiedPlayer) {
        playersQueue[player]?.queue?.remove(player)
        playersQueue.remove(player)
    }
}

var ProxiedPlayer.queueMiniGame : MiniGameInfo?
    get() = PlayerData.playersQueue[this]
    set(value) {
        value?.let {
            this.queueMiniGame?.let {
                PlayerData.playersQueue[this@queueMiniGame]?.queue?.remove(this@queueMiniGame)
                value.queue.add(this@queueMiniGame)
                PlayerData.playersQueue[this@queueMiniGame] = value
            } ?: run {
                throw AlreadyInQueueException()
            }
        } ?: run {
            throw NullPointerException()
        }
    }

var ProxiedPlayer.playingMiniGame : MiniGameInfo?
    get() = PlayerData.playerPlayingMap[this.uniqueId]
    set(value) {
        value?.let {
            PlayerData.playerPlayingMap[this.uniqueId]?.players?.remove(this.uniqueId)
            PlayerData.playerPlayingMap.put(this.uniqueId, it)
            PlayerData.playerPlayingMap[this.uniqueId]!!.players.add(this.uniqueId)
        } ?: run {
            throw NullPointerException()
        }
    }

fun ProxiedPlayer.leaveQueue() {
    PlayerData.leaveQueue(this)
}
