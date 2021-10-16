package org.netherald.quantium.util

import org.bukkit.entity.Player

interface SpectatorUtil {
    companion object {
        lateinit var default : SpectatorUtil
    }

    fun applySpectator(player: Player)

    fun unApplySpectator(player: Player)

}