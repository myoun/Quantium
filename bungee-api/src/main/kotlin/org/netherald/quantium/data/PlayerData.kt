package org.netherald.quantium.data

import net.md_5.bungee.api.connection.ProxiedPlayer
import org.netherald.quantium.MiniGameInfo
import org.netherald.quantium.MiniGameInstance
import java.util.*
import kotlin.collections.HashMap

object PlayerData {
    val playerPlayingMap = HashMap<UUID, MiniGameInstance>()
    val playerQueueMiniGame = HashMap<ProxiedPlayer, MiniGameInfo>()
    val playerReJoinInstance = HashMap<UUID, MiniGameInstance>()

    fun setPlayingMiniGame(player: ProxiedPlayer, value : MiniGameInstance) {
        val mutable = player.playingMiniGame?.players as MutableCollection<UUID>
        mutable.remove(player.uniqueId)
        playerPlayingMap[player.uniqueId] = value
        (value.players as MutableCollection<UUID>).add(player.uniqueId)
    }
}

val ProxiedPlayer.playingMiniGame : MiniGameInstance? get() = PlayerData.playerPlayingMap[uniqueId]

val ProxiedPlayer.queueMiniGame : MiniGameInfo? get() = PlayerData.playerQueueMiniGame[this]

val ProxiedPlayer.reJoinInstance : MiniGameInstance? get() = PlayerData.playerReJoinInstance[this.uniqueId]

fun ProxiedPlayer.clearData() {
    PlayerData.playerPlayingMap -= this.uniqueId
    PlayerData.playerQueueMiniGame -= this
}