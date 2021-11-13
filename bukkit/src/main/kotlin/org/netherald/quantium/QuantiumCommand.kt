package org.netherald.quantium

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.netherald.quantium.data.MiniGameData
import org.netherald.quantium.util.ServerUtil

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

    private fun CommandSender.notPlayer() {
        if (this !is Player) {
            sendMessage("야 ㅡㅡ")
        }
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        when (args.size) {
            1 -> {
                when (args[0].lowercase()) {
                    "help" -> sender.sendHelpMessage()
                    "queue" -> sender.joinHelpMessage()
                    else -> sender.sendHelpMessage()
                }
            }
            2 -> {
                when (args[0]) {
                    "join" -> {
                        if (sender is Player) {
                            MiniGameData.miniGames[args[1]]?.let { miniGame ->
                                miniGame.addPlayer(sender)
                                return true
                            } ?: run {
                                sender.notFountMiniGame()
                                return false
                            }
                        } else {
                            sender.notPlayer()
                        }
                    }
                    else -> sender.sendHelpMessage()
                }
            }
            3 -> {
                when (args[0]) {
                    "join" -> {
                        if (sender is Player) {
                            MiniGameData.miniGames[args[1]]?.let { miniGame ->
                                Bukkit.getPlayer(args[2])?.let {
                                    miniGame.addPlayer(it)
                                } ?: run {
                                    sender.sendOfflinePlayerMessage()
                                }
                                return true
                            } ?: run {
                                sender.notFountMiniGame()
                                return false
                            }
                        } else {
                            sender.notPlayer()
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

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        val out = ArrayList<String>()
        when (args.size) {
            1 -> {
                when (args[0]) {
                    "join" -> {
                        MiniGameData.miniGames.forEach { (_, it) -> out.add(it.name) }
                    }
                }
            }
            2 -> {
                when (args[0]) {
                    "join" -> {
                        ServerUtil.default?.let { serverUtil ->
                            out.addAll(serverUtil.miniGames.filter {
                                it.startsWith(args[1])
                            })
                        } ?: run {
                            MiniGameData.miniGames.forEach { (_, it) ->
                                if (it.name.startsWith(args[1])) {
                                    out.add(it.name)
                                }
                            }
                        }
                    }
                }
            }
            3 -> {
                when (args[0]) {
                    "join" -> {
                        Bukkit.getOnlinePlayers().filter {
                            it.name.startsWith(args[1])
                        }.forEach {
                            out.add(it.name)
                        }
                    }
                }
            }
            else -> {

            }
        }
        return out
    }
}