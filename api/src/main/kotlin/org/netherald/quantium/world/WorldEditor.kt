package org.netherald.quantium.world

import org.bukkit.GameMode

interface WorldEditor {
    companion object {
        lateinit var worldEditor: WorldEditor
    }

    fun cloneWorld(baseWorld : String, newWorld : String)
    fun setGameMode(world : String, gameMode: GameMode)
    fun deleteWorld(world: String)
}