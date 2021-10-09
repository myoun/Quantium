package org.netherald.quantium.dataclass

import org.bukkit.entity.Player
import org.netherald.quantium.MiniGame

val MiniGame.players : ArrayList<Player>
get() {
    MiniGameData.players[this] ?: run {
        MiniGameData.players[this] = ArrayList()
    }
    return MiniGameData.players[this]!!
}

object MiniGameData {
    val miniGames = HashMap<String, MiniGame>()
    val players = HashMap<MiniGame, ArrayList<Player>>()
}