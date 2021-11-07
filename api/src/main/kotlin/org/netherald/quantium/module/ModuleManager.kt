package org.netherald.quantium.module

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.netherald.quantium.Quantium

class ModuleManager {

    lateinit var moduleLoader : ModuleLoader
    fun <T : Event> callEvent(event : T) = Bukkit.getPluginManager().callEvent(event)

    val listenerMap = HashMap<QuantiumModule, Listener>()

    fun registerEvents(listener : Listener, quantiumModule: QuantiumModule) {
        listenerMap[quantiumModule] = listener
        Bukkit.getPluginManager().registerEvents(listener, Quantium.plugin)
    }

    fun getModule(name : String) = Quantium.moduleLoader.getModule(name)

}