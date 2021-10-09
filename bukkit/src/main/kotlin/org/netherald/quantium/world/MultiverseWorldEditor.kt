package org.netherald.quantium.world

import com.onarandombox.MultiverseCore.api.MVWorldManager
import org.bukkit.GameMode

class MultiverseWorldEditor(private val worldManager : MVWorldManager) : WorldEditor {
    override fun cloneWorld(baseWorld: String, newWorld: String) {
        worldManager.cloneWorld(baseWorld, newWorld)
    }

    override fun setGameMode(world: String, gameMode: GameMode) {
        worldManager.getMVWorld(world).gameMode = gameMode
    }

    override fun deleteWorld(world: String) {
        worldManager.deleteWorld(world)
    }
}