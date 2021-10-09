package org.netherald.quantium.util

import org.bukkit.entity.Player
import org.netherald.quantium.MiniGame

fun Player.connectToLobby() {
    PlayerUtil.playerUtil.sendToLobby(this)
}

fun Player.connectToMiniGameServer(miniGame : String) {
    PlayerUtil.playerUtil.sendToMiniGame(this, miniGame)
}

fun Player.connectToServer(serverName : String) {
    PlayerUtil.playerUtil.sendToServer(this, serverName)
}

val MiniGame.miniGamePlayerCount : Int
get() =PlayerUtil.playerUtil.getMiniGamePlayerCount(name)

abstract class PlayerUtil {
    companion object {
        lateinit var playerUtil : PlayerUtil
    }

    abstract fun sendToLobby(player: Player)

    abstract fun sendToMiniGame(player: Player, miniGame : String)

    abstract fun sendToServer(player: Player, serverName : String)

    abstract fun getMiniGamePlayerCount(miniGameName: String) : Int
}