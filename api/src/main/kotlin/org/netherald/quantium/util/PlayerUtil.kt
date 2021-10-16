package org.netherald.quantium.util

import org.bukkit.entity.Player
import org.netherald.quantium.MiniGame

fun Player.connectToLobby() {
    PlayerUtil.default.sendToLobby(this)
}

fun Player.connectToMiniGameServer(miniGame : String) {
    PlayerUtil.default.sendToMiniGame(this, miniGame)
}

fun Player.connectToServer(serverName : String) {
    PlayerUtil.default.sendToServer(this, serverName)
}

interface PlayerUtil {

    companion object {
        lateinit var default : PlayerUtil
    }

    fun sendToLobby(player: Player)

    fun sendToMiniGame(player: Player, miniGame : String)

    fun sendToServer(player: Player, serverName : String)

}