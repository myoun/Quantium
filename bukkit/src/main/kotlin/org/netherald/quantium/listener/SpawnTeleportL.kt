package org.netherald.quantium.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.netherald.quantium.data.*
import org.spigotmc.event.player.PlayerSpawnLocationEvent

class SpawnTeleportL : Listener {
    @EventHandler(priority = EventPriority.LOW)
    fun on(event : PlayerSpawnLocationEvent) {
        event.player.reJoinData ?: run {
            PlayerData.connectionType[event.player] = ConnectionType.LOBBY
            event.player.teleport(QuantiumConfig.lobbyLocation)
        }
    }
}
