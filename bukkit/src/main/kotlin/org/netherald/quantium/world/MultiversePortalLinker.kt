package org.netherald.quantium.world

import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals
import org.bukkit.PortalType
import org.bukkit.World

class MultiversePortalLinker(private val multiverseNetherPortals: MultiverseNetherPortals) : PortalLinker {
    override fun linkNether(world: World, nether: World) {
        multiverseNetherPortals.addWorldLink(world.name, nether.name, PortalType.NETHER)
        multiverseNetherPortals.addWorldLink(nether.name, world.name, PortalType.NETHER)
    }

    override fun linkEnder(world: World, ender: World) {
        multiverseNetherPortals.addWorldLink(world.name, ender.name, PortalType.ENDER)
        multiverseNetherPortals.addWorldLink(ender.name, world.name, PortalType.ENDER)
    }

    override fun link(world: World, nether: World, ender: World) {
        linkNether(world, nether)
        linkEnder(world, ender)
    }
}