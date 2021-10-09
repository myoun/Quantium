package org.netherald.quantium.util

import org.bukkit.entity.Player
import org.netherald.quantium.dataclass.MiniGameData
import org.netherald.quantium.dataclass.QuantiumConfig
import org.netherald.quantium.dataclass.players

class QuantiumPlayerUtil : PlayerUtil() {
    override fun sendToLobby(player: Player) {
        player.teleport(QuantiumConfig.lobbyLocation)
    }

    override fun sendToMiniGame(player: Player, miniGame: String) {

    }

    override fun sendToServer(player: Player, serverName: String) {

    }

    override fun getMiniGamePlayerCount(miniGameName: String) : Int {
        return MiniGameData.miniGames[miniGameName]!!.players.size
    }
}