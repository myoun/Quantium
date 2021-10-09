package org.netherald.quantium.listener

import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import org.netherald.quantium.data.leaveQueue

class DisconnectL : Listener {
    @EventHandler
    fun on(event : PlayerDisconnectEvent) {
        event.player.leaveQueue()
    }
}