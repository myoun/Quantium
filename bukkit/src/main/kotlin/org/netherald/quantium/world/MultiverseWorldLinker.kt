package org.netherald.quantium.world

import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals
import org.bukkit.PortalType

class MultiverseWorldLinker(private val multiverseNetherPortals: MultiverseNetherPortals) : WorldLinker {
    override fun linkNether(world: String, nether: String) {
        multiverseNetherPortals.addWorldLink(world, nether, PortalType.NETHER)
        multiverseNetherPortals.addWorldLink(nether, world, PortalType.NETHER)
    }

    override fun linkEnder(world: String, ender: String) {
        multiverseNetherPortals.addWorldLink(world, ender, PortalType.ENDER)
        multiverseNetherPortals.addWorldLink(ender, world, PortalType.ENDER)
    }
}