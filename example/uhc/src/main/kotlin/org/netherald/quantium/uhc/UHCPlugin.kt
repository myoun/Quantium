package org.netherald.quantium.uhc

import com.onarandombox.MultiverseCore.MultiverseCore
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.WorldType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.MiniGameInstance
import org.netherald.quantium.registerMiniGame
import kotlin.random.Random

class UHCPlugin : JavaPlugin() {

    private val teleportSizePath = "random-teleport-size"
    private val worldName = "uhc_playing_world"
    private val netherName = "uhc_playing_world_nether"
    private val enderName = "uhc_playing_world_the_end"

    val mvWorldManager = (server.pluginManager.getPlugin("Multiverse-Core") as MultiverseCore).mvWorldManager

    private fun addWorld(name : String, env : World.Environment) : World {
        val successful = mvWorldManager
            .addWorld(
                name, env, null, WorldType.NORMAL, true, null
            )
        if (successful) {
            return mvWorldManager.getMVWorld(name).cbWorld
        } else {
            throw RuntimeException("failed to make world")
        }
    }

    override fun onEnable() {

        saveDefaultConfig()
        val randomTeleportSize = config.getInt(teleportSizePath)

        lateinit var miniGameInstance : MiniGameInstance

        lateinit var world : World

        val randomTeleport = fun Player.() {
            lateinit var location : Location
            loop@ while (true) {
                val baseLocation = Location(
                    world,
                    Random.nextInt(randomTeleportSize).toDouble() - (randomTeleportSize/2),
                    256.0,
                    Random.nextInt(randomTeleportSize).toDouble() - (randomTeleportSize/2)
                )
                for (i in 256 downTo 0) {
                    val now = baseLocation.clone().apply { y = i.toDouble() }
                    val under = now.clone().apply { y-- }
                    if (under.block.type != Material.AIR) {
                        if (under.block.type == Material.LAVA) continue@loop
                        location = now
                        break@loop
                    }
                }
            }
            teleport(location)
        }

        registerMiniGame("uhc_25-100", 1, 100) {

            miniGameInstance = this
            enableRejoin = false

            listener(PlayerDeathEvent::class.java) {
                event.entity.applySpectator()
            }

            onPlayerDisconnected {
                event.quitMessage = "플레이어 ${event.player.name}이 퇴장하였습니다"
            }

            onPlayerKicked {
                event.reason = "플레이어 ${event.player.name}이 강퇴되었습니다"
            }

            onInstanceCreated {
                this@registerMiniGame.UnSafe().world = addWorld(worldName, World.Environment.NORMAL).also {
                    world = it
                }
                this@registerMiniGame.UnSafe().worldNether = addWorld(netherName, World.Environment.NETHER)
                this@registerMiniGame.UnSafe().worldEnder = addWorld(enderName, World.Environment.THE_END)
            }

            onStart {
                players.forEach { player -> player.randomTeleport() }
            }
        }

        server.pluginManager.registerEvents(object : Listener {
            @EventHandler
            fun onJoin(e : PlayerJoinEvent) {
                if (!(miniGameInstance.isStarted || miniGameInstance.isFinished)) {
                    miniGameInstance.addPlayer(e.player)
                }
            }
        }, this)
    }
}