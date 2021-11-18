package org.netherald.quantium.testplugin

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.MiniGameInstance
import org.netherald.quantium.data.QuantiumConfig
import org.netherald.quantium.registerMiniGame

class TestPlugin : JavaPlugin() {

    private val teleportSizePath = "random-teleport-size"
    private val worldName = "uhc_playing_world"
    private val netherName = "uhc_playing_world_nether"
    private val endName = "uhc_playing_world_the_end"

    override fun onEnable() {
        lateinit var miniGameInstance : MiniGameInstance

        registerMiniGame("TEST-MiniGame", 1, 100) {

            miniGameInstance = this
            enableRejoin = false

            teamSetting {
                disable()
            }

            applyNewScoreBoard("TestMiniGame") {
                "Hello" to 2
                "World" to 1
            }

            onStart {
                applyScoreBoard() {
                    loopTask(10 downTo 1, 1, 20) { i ->
                        displayName = "It'll turn off in $i second"
                    }
                    2 to "GoodBye"
                }
                runTaskLater(200) {
                    stopGame()
                }
            }
        }

        if (!(QuantiumConfig.Bungee.enable)) {
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
}