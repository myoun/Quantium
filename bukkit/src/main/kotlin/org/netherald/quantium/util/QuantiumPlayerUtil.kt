package org.netherald.quantium.util

import org.bukkit.entity.Player
import org.netherald.quantium.data.*

class QuantiumPlayerUtil : PlayerUtil {

    override fun sendToLobby(player: Player) {
        PlayerData.UnSafe.clearData(player)
        PlayerData.connectionType[player] = ConnectionType.LOBBY
        player.teleport(QuantiumConfig.lobbyLocation)
    }

    override fun sendToMiniGame(player: Player, miniGame: String) {
        MiniGameData.miniGames[miniGame]?.let { minigame ->
            minigame.instances.filter {
                if (!it.isStarted) true
                else if (!it.isFinished) false
                else false
            }.random().addPlayer(player)
            PlayerData.connectionType[player] = ConnectionType.MINIGAME
        } ?: run {
            throw IllegalStateException("not found mini-game")
        }
    }

    override fun sendToServer(player: Player, serverName: String) {
        throw RuntimeException("It is not Bungee")
    }
}