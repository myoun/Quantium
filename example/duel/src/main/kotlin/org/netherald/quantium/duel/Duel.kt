package org.netherald.quantium.duel

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.registerMiniGame

class Duel : JavaPlugin() {
    override fun onEnable() {
        registerMiniGame("Duel-1vs1", 2, 2) {

            worldSetting {
                baseWorld = Bukkit.getWorld("world")
            }

            teamSetting {
                enable = true
                teamSize = 1
            }

            val redSpawnLocation = Location(world, -100.0, 64.0, -100.0)
            val blueSpawnLocation = Location(world, 100.0, 64.0, 100.0)

            lateinit var winner : Player
            lateinit var loser : Player

            onStart {
                teamMatch()
                team[0][0].teleport(redSpawnLocation)
                team[1][0].teleport(blueSpawnLocation)

                players.forEach {
                    it.inventory.addItem(ItemStack(Material.DIAMOND_SWORD))
                    it.inventory.helmet = ItemStack(Material.DIAMOND_HELMET)
                    it.inventory.chestplate = ItemStack(Material.DIAMOND_CHESTPLATE)
                    it.inventory.leggings = ItemStack(Material.DIAMOND_LEGGINGS)
                    it.inventory.boots = ItemStack(Material.DIAMOND_BOOTS)
                }
            }

            listener(PlayerDeathEvent::class.java) {
                winner = players.filter { it != event.entity }[0]
                loser = event.entity
                stopGame()
            }

            onStop {
                winner.sendMessage("you are winner")
                loser.sendMessage("you was dinner")
            }


        }
    }
}
