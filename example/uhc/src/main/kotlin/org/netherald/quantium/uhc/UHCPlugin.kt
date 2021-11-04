package org.netherald.quantium.uhc

import com.onarandombox.MultiverseCore.MultiverseCore
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.util.MiniGameBuilderUtil
import org.netherald.quantium.MiniGameInstance
import org.netherald.quantium.registerMiniGame
import kotlin.random.Random

class UHCPlugin : JavaPlugin() {

    private val teleportSizePath = "random-teleport-size"
    private val worldName = "uhc_playing_world"
    private val netherName = "uhc_playing_world_nether"
    private val enderName = "uhc_playing_world_the_end"

    private val mvWorldManager =
        (server.pluginManager.getPlugin("Multiverse-Core") as MultiverseCore).mvWorldManager!!

    override fun onEnable() {

        saveDefaultConfig()
        val randomTeleportSize = config.getInt(teleportSizePath)

        val seed = Random.nextLong()

        val newWorld = fun (name : String, env : World.Environment) : World {
            val successful = mvWorldManager.addWorld(
                name, env, seed.toString(), WorldType.NORMAL, true, null
            )
            if (successful) {
                val world = mvWorldManager.getMVWorld(name)
                logger.info("world ${world.name} is added")
                return world.cbWorld!!
            } else {
                throw RuntimeException("failed to make world")
            }
        }

        val addNewWorld = fun MiniGameBuilderUtil.(name : String, env : World.Environment) {
            val world = newWorld(name, env)
            val addWorldData = fun (worldType : MiniGameInstance.AddWorldType) = addWorld(world, worldType)
            when (env) {
                World.Environment.NORMAL -> addWorldData(MiniGameInstance.AddWorldType.NORMAL)
                World.Environment.NETHER -> addWorldData(MiniGameInstance.AddWorldType.NETHER)
                World.Environment.THE_END -> addWorldData(MiniGameInstance.AddWorldType.ENDER)
                else -> throw RuntimeException("wrong world type")
            }
        }

        lateinit var miniGameInstance : MiniGameInstance

        val randomTeleport = fun Player.() {
            lateinit var location : Location
            val generateRandom = fun () = Random.nextInt(randomTeleportSize).toDouble() - (randomTeleportSize/2) + 0.5
            loop@ while (true) {
                val baseLocation = Location(miniGameInstance.world, generateRandom(), 256.0, generateRandom())
                for (i in 256 downTo 0) {
                    val now = baseLocation.clone().apply { y = i.toDouble() }
                    val under = now.clone().apply { y-- }
                    if (under.block.type != Material.AIR) {
                        if (under.block.type == Material.LAVA) continue@loop
                        if (under.block.type == Material.WATER) continue@loop
                        location = now
                        break@loop
                    } else if (under.y == 0.0) { continue@loop }
                }
            }
            teleport(location)
        }









        registerMiniGame("uhc_25-100", 1, 100) {

            miniGameInstance = this
            enableRejoin = false

            onInstanceCreated {

                addNewWorld(worldName, World.Environment.NORMAL)
                addNewWorld(netherName, World.Environment.NETHER)
                addNewWorld(enderName, World.Environment.THE_END)

                spawn = Location(world, 0.0, 100.0, 0.0)
            }

            teamSetting {
                disable()
            }

            listener(PlayerDeathEvent::class.java) {
                event.entity.applySpectator()
            }

            listener(PlayerRespawnEvent::class.java) {
                event.player.killer?.let { event.player.teleport(it) }
            }

            onPlayerAdded {
                broadCast("${it.name} 님이 게임에 참여하였습니다")
            }

            onPlayerRemoved {
                broadCast("${it.name} 님이 게임에서 나가였습니다")
            }

            onPlayerDisconnected {
                event.quitMessage = "플레이어 ${event.player.name}이 퇴장하였습니다"
            }

            onPlayerKicked {
                event.reason = "플레이어 ${event.player.name}이 강퇴되었습니다"
            }

            onStart {
                players.forEach { it.randomTeleport() }
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