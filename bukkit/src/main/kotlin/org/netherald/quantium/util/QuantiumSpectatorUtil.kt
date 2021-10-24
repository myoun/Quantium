package org.netherald.quantium.util

import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.netherald.quantium.data.playingGame

class QuantiumSpectatorUtil : SpectatorUtil {
    companion object {
        val spectatorData = HashSet<Player>()
    }
    override fun applySpectator(player: Player) {
        player.gameMode = GameMode.SPECTATOR
        spectatorData += player
    }

    override fun unApplySpectator(player: Player) {
        player.playingGame?.let {
            player.gameMode = it.defaultGameMode
        }
        spectatorData -= player
    }
}