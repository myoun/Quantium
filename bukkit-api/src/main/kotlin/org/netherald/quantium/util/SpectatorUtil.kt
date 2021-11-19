package org.netherald.quantium.util

import org.bukkit.entity.Player

interface SpectatorUtil {
    companion object {
        lateinit var default : SpectatorUtil
    }
    val spectators : Collection<Player>

    fun applySpectator(player: Player)

    fun unApplySpectator(player: Player)

}