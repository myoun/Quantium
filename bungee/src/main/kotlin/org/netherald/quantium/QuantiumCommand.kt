package org.netherald.quantium

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.TabExecutor
import org.netherald.quantium.data.MiniGameData
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.ArrayList

class QuantiumCommand : Command("quantiumproxy", "Quantium.command", "qp"), TabExecutor {

    fun CommandSender.notFoundMiniGame() {
        sendMessage("Not found mini-game!")
    }

    fun CommandSender.notFoundInstance() {
        sendMessage("Not found instance!")
    }

    fun CommandSender.wrongUUID() {
        sendMessage("Wrong UUID!")
    }

    override fun execute(sender: CommandSender, args: Array<out String>) {
        when (args.size) {
            1 -> {
                when (args[0].lowercase()) {
                    "minigames" -> {
                        sender.sendMessage("Mini-games : ${MiniGameData.miniGames.map { it.key }}")
                    }
                }
            }
            3 -> {
                when (args[0].lowercase()) {
                    "minigame" -> {
                        val miniGame = MiniGameData.miniGames[args[1]] ?: run {
                            sender.notFoundMiniGame()
                            return
                        }
                        when (args[2].lowercase()) {
                            "info" -> {
                                sender.sendMessage("""
                                    player-count: ${miniGame.players.size}
                                """.trimIndent())
                            }
                            "instances" -> {
                                sender.sendMessage("""
                                    instances : ${miniGame.instances.map { it.uuid }}
                                """.trimIndent())
                            }
                            "join" -> {
                                if (sender is ProxiedPlayer) {
                                    miniGame.addPlayer(sender)
                                }
                            }
                        }
                    }
                    "instance" -> {
                        lateinit var uuid: UUID
                        try { uuid = UUID.fromString(args[2]) } catch (e : IllegalArgumentException) {
                            sender.wrongUUID()
                            return
                        }

                        val instance = MiniGameData.instances[uuid] ?: run {
                            sender.notFoundInstance()
                            return
                        }
                        when (args[2].lowercase()) {
                            "info" -> {
                                sender.sendMessage("""
                                    uuid: $instance.uuid
                                    mini-game: ${instance.miniGame}
                                    is Started?: ${instance.isStarted}
                                    is Stopped?: ${instance.isStopped}
                                """.trimIndent()
                                )
                            }
                            "join" -> {
                                if (sender is ProxiedPlayer) {
                                    instance.addPlayer(sender)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private val functions = ArrayList<String>().apply {
        add("minigames")
        add("info")
        add("instance")
        add("minigame")
    }

    private val miniGameFunctions = ArrayList<String>().apply {
        add("info")
        add("instances")
        add("join")
    }

    private val instanceFunctions = ArrayList<String>().apply {
        add("info")
        add("join")
    }

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): MutableIterable<String> {
        val out = ArrayList<String>()
        when (args.size) {
            1 -> {
                functions.forEach { if (it.startsWith(args[0].lowercase())) out.add(it) }
            }
            2 -> {
                val args1 = args[1].lowercase()
                when (args[0].lowercase()) {
                    "minigame" -> {
                        out.addAll(MiniGameData.miniGames.keys.filter { it.startsWith(args1) })
                    }
                    "instance" -> {
                        out.addAll(MiniGameData.instances.keys.map { it.toString() }.filter { it.startsWith(args1) })
                    }
                }
            }
            3 -> {
                val args2 = args[2].lowercase()
                when (args[0].lowercase()) {
                    "minigame" -> {
                        out.addAll(miniGameFunctions.filter { it.startsWith(args2) })
                    }
                    "instance" -> {
                        out.addAll(instanceFunctions.filter { it.startsWith(args2) })
                    }
                }
            }
        }
        return out
    }
}