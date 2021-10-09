package org.netherald.quantium.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.netherald.quantium.dataclass.PlayerData
import org.netherald.quantium.dataclass.QuantiumConfig
import org.netherald.quantium.dataclass.reJoinData

class ConnectedListener : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun on(event : PlayerJoinEvent) {
        event.player.reJoinData ?: run {
            event.player.teleport(QuantiumConfig.lobbyLocation)
        }
    }
}
