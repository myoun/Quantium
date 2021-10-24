package org.netherald.quantium.listener

import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent
import org.netherald.quantium.data.playingGame
import org.netherald.quantium.util.QuantiumSpectatorUtil

class RespawnL : Listener {
    @EventHandler(priority = EventPriority.LOW)
    fun on(event : PlayerRespawnEvent) {
        event.player.playingGame?.let { instance ->
            instance.worldSetting.spawn?.let {
                event.respawnLocation = it
            }
            if (QuantiumSpectatorUtil.spectatorData.contains(event.player)) {
                event.player.gameMode = GameMode.SPECTATOR
            }
        }
    }
}