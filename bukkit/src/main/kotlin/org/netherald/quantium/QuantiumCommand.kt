package org.netherald.quantium

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.netherald.quantium.data.MiniGameData

class QuantiumCommand : CommandExecutor, TabCompleter {

    private fun CommandSender.sendHelpMessage() {
        sendMessage("/qb help / print help message")
        queueHelpMessage()
    }

    private fun CommandSender.queueHelpMessage() {
        sendMessage("/qb queue <player> / move queue to player")
        sendMessage("/qb queue <MiniGameName> [player] / add queue")
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
                    "queue" -> sender.queueHelpMessage()
                    else -> sender.sendHelpMessage()
                }
            }
            2 -> {
                when (args[0]) {
                    "queue" -> {
                        if (sender is Player) {
                            MiniGameData.miniGames[args[1]]?.let { minigame ->
                                minigame.addPlayer(sender)
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
                    "queue" -> {
                        val player = Bukkit.getPlayer(args[2])
                        player ?: run {
                            sender.sendOfflinePlayerMessage()
                            return false
                        }
                        MiniGameData.miniGames[args[1]]?.let { minigame ->
                            minigame.addPlayer(player)
                            return true
                        } ?: run {
                            sender.notFountMiniGame()
                            return false
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
        return ArrayList()
    }
}