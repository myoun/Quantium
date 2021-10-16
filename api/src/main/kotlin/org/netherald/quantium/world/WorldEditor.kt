package org.netherald.quantium.world

import org.bukkit.GameMode
import org.bukkit.World

interface WorldEditor {
    companion object {
        lateinit var default : WorldEditor
    }

    fun cloneWorld(baseWorld : World, newWorld : String) : World
    fun setGameMode(world : World, gameMode: GameMode)
    fun deleteWorld(world: World)
}