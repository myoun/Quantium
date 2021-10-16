package org.netherald.quantium.world

interface PortalLinker {
    companion object {
        lateinit var default: PortalLinker
    }
    fun linkNether(world : String, nether : String)
    fun linkEnder(world : String, ender : String)
}