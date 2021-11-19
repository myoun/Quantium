package org.netherald.quantium.module

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.netherald.quantium.Quantium

class ModuleManager {

    fun <T : Event> callEvent(event : T) = Bukkit.getPluginManager().callEvent(event)

    val listeners : Map<QuantiumModule, List<Listener>> get() = listenerMap
    private val listenerMap = HashMap<QuantiumModule, ArrayList<Listener>>()
    private val listenerMap2 = HashMap<Listener, QuantiumModule>()

    fun registerEvents(listener : Listener, quantiumModule: QuantiumModule) {
        listenerMap[quantiumModule] ?: run { listenerMap[quantiumModule] = ArrayList() }
        listenerMap[quantiumModule]!! += listener
        listenerMap2[listener] = quantiumModule
        Bukkit.getPluginManager().registerEvents(listener, Quantium.plugin)
    }

    fun unregisterEvents(quantiumModule: QuantiumModule) {
        listenerMap[quantiumModule]!!.forEach {
            unregisterEvents(it)
        }
    }

    fun unregisterEvents(listener: Listener) {
        if (listenerMap2.containsKey(listener)) {
            HandlerList.unregisterAll(listener)
            listenerMap -= listenerMap2[listener]!!
            listenerMap2 -= listener
        }
    }

    fun getModule(name : String) = Quantium.moduleLoader.getModule(name)

}