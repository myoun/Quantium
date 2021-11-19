package org.netherald.quantium.world

import com.onarandombox.MultiverseCore.api.MVWorldManager
import org.bukkit.Bukkit
import org.bukkit.World
import org.netherald.quantium.exception.WorldCloneFailedException

class MultiverseWorldEditor(private val worldManager : MVWorldManager) : WorldEditor {
    override fun cloneWorld(baseWorld: World, newWorld: String) : World {
        if (worldManager.cloneWorld(baseWorld.name, newWorld)) {
            return Bukkit.getWorld(newWorld)!!
        } else {
            throw WorldCloneFailedException()
        }
    }

    override fun deleteWorld(world: World) {
        worldManager.deleteWorld(world.name)
    }
}