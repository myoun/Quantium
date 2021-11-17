package org.netherald.quantium

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.data.ModuleData
import org.netherald.quantium.event.BlockedServerEvent
import org.netherald.quantium.event.UnBlockedServerEvent
import org.netherald.quantium.module.ModuleLoader
import org.netherald.quantium.module.ModuleManager
import org.netherald.quantium.module.QuantiumModule
import org.netherald.quantium.module.scheduler.QuantiumScheduler

object Quantium {
    lateinit var plugin : JavaPlugin
    val modules : Map<String, QuantiumModule> get() = ModuleData.modules
    lateinit var moduleLoader : ModuleLoader
    val moduleManager = ModuleManager()
    val scheduler = QuantiumScheduler()

    var isServerBlocked = true
        set(value) {
            if (isServerBlocked != value) {
                if (value) {
                    Bukkit.getPluginManager().callEvent(BlockedServerEvent())
                } else {
                    Bukkit.getPluginManager().callEvent(UnBlockedServerEvent())
                }
                field = value
            }
        }
}