package org.netherald.quantium

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.netherald.quantium.data.*
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class QuantiumCommand : CommandExecutor, TabCompleter {

    private fun CommandSender.sendHelpMessage() {
        sendMessage("/qb help / print help message")
        joinHelpMessage()
    }

    private fun CommandSender.joinHelpMessage() {
        sendMessage("/qb join <MiniGameName> [player] / add queue")
    }

    private fun CommandSender.sendOfflinePlayerMessage() {
        sendMessage("A player is offline")
    }

    private fun CommandSender.notFountMiniGame() {
        sendMessage("Not found mini-game")
    }

    private fun CommandSender.notFountModule() {
        sendMessage("Not found module")
    }

    private fun CommandSender.notPlayer() {
        if (this !is Player) {
            sendMessage("야 ㅡㅡ")
        }
    }

    private fun CommandSender.notUUID() {
        sendMessage("Not UUID!")
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        when (args.size) {
            1 -> {
                when (args[0]) {
                    "minigames" -> {
                        sender.sendMessage("""
                            minigames : ${MiniGameData.miniGames.map { it.key }}
                        """.trimIndent())
                    }
                    "modules" -> {
                        sender.sendMessage("""
                            modules : ${Quantium.modules.map { it.key }}
                        """.trimIndent())
                    }
                    "instances" -> {
                        sender.sendMessage("""
                            instances : ${MiniGameData.instances.map { it.key }}
                        """.trimIndent())
                    }
                }
            }

            3 -> {
                when (args[0].lowercase()) {
                    "minigame" -> {
                        val miniGame = MiniGameData.miniGames[args[1]] ?: run {
                            sender.notFountMiniGame()
                            return false
                        }
                        when (args[2].lowercase()) {
                            "join" -> {
                                if (sender !is Player) {
                                    sender.notPlayer()
                                    return false
                                }
                                miniGame.addPlayer(sender)
                            }
                            "info" -> {
                                sender.sendMessage("""
                                    name : ${miniGame.name}
                                    player-count : ${miniGame.players.size}
                                    queue-size : ${miniGame.queue.size}
                                """.trimIndent())
                            }
                            "instances" -> {
                                sender.sendMessage("""
                                    instances : ${miniGame.instances}
                                """.trimIndent())
                            }
                        }
                    }
                    "instance" -> {
                        try {
                            UUID.fromString(args[1])
                        } catch (e : IllegalArgumentException) {
                            sender.notUUID()
                            return false
                        }
                        val instance = MiniGameData.instances[UUID.fromString(args[1])] ?: run {
                            sender.notFountMiniGame()
                            return false
                        }

                        when(args[2].lowercase()) {
                            "join" -> {
                                if (sender !is Player) {
                                    sender.notPlayer()
                                    return false
                                }
                                instance.addPlayer(sender)
                            }
                            "info" -> {
                                sender.sendMessage("""
                                    uuid : ${instance.uuid}
                                    player-count : ${instance.players.size}
                                    is-started : ${instance.isStarted}
                                    is-finished : ${instance.isFinished}
                                """.trimIndent())
                            }
                            "players" -> {
                                sender.sendMessage("""
                                    players : ${instance.players}
                                """.trimIndent())
                            }
                        }
                    }
                    "player" -> {
                        val player = Bukkit.getPlayer(args[1]) ?: run {
                            sender.sendOfflinePlayerMessage()
                            return false
                        }
                        when(args[2].lowercase()) {
                            "info" -> {
                                sender.sendMessage("""
                                    connection-type : ${player.connectionType}
                                    playing-instance : ${player.playingGame}
                                    instance-minigame : ${player.playingGame?.miniGame}
                                    rejoin-data : ${player.reJoinData}
                                """.trimIndent())
                            }
                        }
                    }
                    "module" -> {
                        val module = Quantium.modules[args[1]] ?: run {
                            sender.notFountModule()
                            return false
                        }
                        when(args[2].lowercase()) {
                            "info" -> {
                                sender.sendMessage("""
                                    name : ${module.name}
                                    enabled : ${module.isEnabled}
                                """.trimIndent())
                            }
                        }
                    }
                    else -> sender.sendHelpMessage()
                }
            }
            else -> {
                sender.sendHelpMessage()
            }
        }
        return false
    }

    private val types = ArrayList<String>().apply {
        add("minigames")
        add("modules")
        add("instances")
        add("minigame")
        add("instance")
        add("player")
        add("module")
    }

    private val functions = HashMap<String, List<String>>().apply {
        this["minigame"] = ArrayList<String>().apply {
            add("info")
            add("instances")
            add("join")
        }
        this["instance"] = ArrayList<String>().apply {
            add("info")
            add("join")
            add("players")
        }
        this["module"] = ArrayList<String>().apply {
            add("info")
        }
        this["player"] = ArrayList<String>().apply {
            add("info")
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        val out = ArrayList<String>()
        when (args.size) {
            1 -> {
                types.forEach { if (it.startsWith(args[0])) out.add(it) }
            }
            2 -> {
                when (args[0].lowercase()) {
                    "minigame" -> {
                        MiniGameData.miniGames.forEach { (_, it) ->
                            if (it.name.startsWith(args[1])) {
                                out.add(it.name)
                            }
                        }
                    }
                    "instance" -> {
                        MiniGameData.miniGames.forEach { (_, it) ->
                            if (it.name.startsWith(args[1])) {
                                out.add(it.name)
                            }
                        }
                    }
                    "module" -> {
                        out.addAll(Quantium.modules.map { it.key }.filter {
                            it.startsWith(args[1])
                        })
                    }
                    "player" -> {
                        out.addAll(Bukkit.getOnlinePlayers().map{ it.name }.filter { it.startsWith(args[1]) }.toList())
                    }
                }
            }
            3 -> {
                functions[args[1].lowercase()]?.let { list ->
                    out.addAll(list.filter { it.startsWith(args[2].lowercase()) })
                }
            }
        }
        return out
    }
}