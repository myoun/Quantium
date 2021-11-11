package org.netherald.quantium

import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.data.ModuleData
import org.netherald.quantium.module.ModuleLoader
import org.netherald.quantium.module.ModuleManager
import org.netherald.quantium.module.QuantiumModule
import org.netherald.quantium.module.QuantiumScheduler

object Quantium {
    lateinit var plugin : JavaPlugin
    val modules : Map<String, QuantiumModule> get() = ModuleData.modules
    lateinit var moduleLoader : ModuleLoader
    val moduleManager = ModuleManager()
    val scheduler = QuantiumScheduler()
}