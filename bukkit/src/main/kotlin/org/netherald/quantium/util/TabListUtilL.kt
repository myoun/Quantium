package org.netherald.quantium.util

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.MiniGameInstance
import org.netherald.quantium.data.reJoinData

class TabListUtilL(private val plugin: JavaPlugin) : Listener {

    companion object {
        val targetMiniGame = HashSet<MiniGameInstance>()
    }

    @EventHandler
    fun onJoin(event : PlayerJoinEvent) {
        event.player.reJoinData?.let { miniGame ->
            if (!targetMiniGame.contains(miniGame)) return

            val ignorePlayers = ArrayList<Player>(Bukkit.getOnlinePlayers())
            ignorePlayers.removeAll(event.player.world.players.toSet())
            ignorePlayers.removeAll(miniGame.players)

            ignorePlayers.forEach { player ->
                event.player.hidePlayer(plugin, player)
                player.hidePlayer(plugin, event.player)
            }

        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onWorldSwitch(event: PlayerTeleportEvent) {
        if (event.from.world != event.to?.world) {
            event.to?.let { _ ->
                event.from.world?.players?.forEach { event.player.hidePlayer(plugin, it) }
                event.to!!.world?.players?.forEach { event.player.showPlayer(plugin, it) }
            }
        }
    }
}