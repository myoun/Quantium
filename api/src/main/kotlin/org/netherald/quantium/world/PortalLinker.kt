package org.netherald.quantium.world

import org.bukkit.World

interface PortalLinker {
    companion object {
        lateinit var default: PortalLinker
    }
    fun linkNether(world : World, nether : World)
    fun linkEnder(world : World, ender : World)
    fun link(world : World, nether : World, ender : World)
}