package org.netherald.quantium.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.netherald.quantium.Channels
import org.netherald.quantium.Quantium
import org.netherald.quantium.util.PluginMessageServerUtil

class PlayerJoinL : Listener {
    @EventHandler
    fun on(event : PlayerJoinEvent) {
        PluginMessageServerUtil.queuedMessage.forEach {
            event.player.sendPluginMessage(Quantium.plugin, Channels.mainChannel, it)
        }
        PluginMessageServerUtil.queuedMessage.clear()
    }
}