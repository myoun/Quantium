package org.netherald.quantium

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.TabExecutor
import org.netherald.quantium.data.MiniGameData
import org.netherald.quantium.data.playerQueueMiniGame
import org.netherald.quantium.data.playingMiniGame
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

    fun CommandSender.notFoundPlayer() {
        sendMessage("Not found Player!")
    }

    fun CommandSender.wrongUUID() {
        sendMessage("Wrong UUID!")
    }

    fun CommandSender.alreadyInQueue() {
        sendMessage("Already in queue!")
    }

    override fun execute(sender: CommandSender, args: Array<out String>) {
        debug("${sender.name} commanded ${args.toList()}")
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
                                    try {
                                        miniGame.addPlayer(sender)
                                    } catch(e : IllegalStateException) {
                                        sender.alreadyInQueue()
                                    }
                                }
                            }
                        }
                    }
                    "instance" -> {
                        lateinit var uuid: UUID
                        try { uuid = UUID.fromString(args[1]) } catch (e : IllegalArgumentException) {
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
                    "player" -> {
                        val player = ProxyServer.getInstance().getPlayer(args[1]) ?: run {
                            sender.notFoundPlayer()
                            return
                        }
                        when (args[2].lowercase()) {
                            "info" -> {
                                sender.sendMessage("""
                                    playing: ${player.playingMiniGame}
                                    queue-minigame: ${player.playerQueueMiniGame}
                                """.trimIndent())
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

    private val functionMap = HashMap<String, ArrayList<String>>().apply {
        this["minigame"] = ArrayList<String>().apply {
            add("info")
            add("instances")
            add("join")
        }
        this["instance"] = ArrayList<String>().apply {
            add("info")
            add("join")
        }
        this["player"] = ArrayList<String>().apply {
            add("info")
        }
    }

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): MutableIterable<String> {
        val out = ArrayList<String>()
        when (args.size) {
            1 -> {
                functions.forEach { if (it.startsWith(args[0].lowercase())) out.add(it) }
            }
            2 -> {
                val arg1 = args[1].lowercase()
                when (args[0].lowercase()) {
                    "minigame" -> {
                        out.addAll(MiniGameData.miniGames.keys.filter { it.startsWith(arg1) })
                    }
                    "instance" -> {
                        out.addAll(MiniGameData.instances.keys.map { it.toString() }.filter { it.startsWith(arg1) })
                    }
                    "player" -> {
                        out.addAll(ProxyServer.getInstance().players.map { it.name }.filter { it.startsWith(arg1) })
                    }
                }
            }
            3 -> {
                functionMap[args[0].lowercase()]?.let { functionName ->
                    out.addAll(functionName.filter { it.startsWith(args[2]) })
                }
            }
        }
        return out
    }
}