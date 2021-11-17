package org.netherald.quantium.listener

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.netherald.quantium.Quantium
import org.netherald.quantium.data.MiniGameData

class PluginDisableL : Listener {
    @EventHandler
    fun on(event : PluginDisableEvent) {
        Quantium.modules.forEach { (_, module) ->
            if (module.classLoader.pluginDepend.contains(event.plugin)) {
                module.setEnabled(false)
            }
        }
        MiniGameData.miniGames.iterator().forEach { (_, miniGame) ->
            if (miniGame.owner == event.plugin) {
                println("""
                    
                    Plugin ${event.plugin.name} is disabled.
                    That plugin has MiniGame :(
                    So, delete minigame
                    
                """.trimIndent())
                miniGame.maxInstanceSize = 0
                miniGame.instances.iterator().forEach {
                    it.delete()
                }
            }
        }
    }
}