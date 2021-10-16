package org.netherald.quantium.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.netherald.quantium.data.QuantiumConfig
import org.netherald.quantium.data.reJoinData
import org.spigotmc.event.player.PlayerSpawnLocationEvent

class SpawnTeleportL : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun on(event : PlayerSpawnLocationEvent) {
        event.player.reJoinData ?: run {
            event.player.teleport(QuantiumConfig.lobbyLocation)
        }
    }
}
