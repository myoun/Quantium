package org.netherald.quantium

import net.md_5.bungee.api.CommandSender
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
                                sender.sendMessage(
                                    """
                                    
                                """.trimIndent()
                                )
                            }
                            "instances" -> {
                                TODO()
                            }
                        }
                    }
                    "instance" -> {
                        lateinit var uuid: UUID
                        kotlin.runCatching { uuid = UUID.fromString(args[2]) }.exceptionOrNull()?.let {
                            if (it is IllegalArgumentException) {
                                sender.wrongUUID()
                            } else {
                                throw it
                            }
                            return
                        }

                        val miniGame = MiniGameData.instances[uuid] ?: run {
                            sender.notFoundInstance()
                            return
                        }
                        when (args[2].lowercase()) {
                            "info" -> {
                                sender.sendMessage(
                                    """
                                    
                                """.trimIndent()
                                )
                                TODO()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): MutableIterable<String> {
        val out = ArrayList<String>()
        when (args.size) {
            1 -> {
                when (args[0].lowercase()) {
                    "minigames" -> {

                    }
                }
            }
            2 -> {
                when (args[0].lowercase()) {
                    "info" -> {

                    }
                    "instances" -> {

                    }
                }
            }
        }
        return out
    }
}