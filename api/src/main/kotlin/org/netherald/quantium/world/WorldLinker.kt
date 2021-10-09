package org.netherald.quantium.world

interface WorldLinker {
    companion object {
        lateinit var worldLinker: WorldLinker
    }
    fun linkNether(world : String, nether : String)
    fun linkEnder(world : String, ender : String)
}