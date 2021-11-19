package org.netherald.quantium.world

import org.bukkit.World

interface WorldEditor {
    companion object {
        var default : WorldEditor? = null
    }

    fun cloneWorld(baseWorld : World, newWorld : String) : World
    fun deleteWorld(world: World)
}