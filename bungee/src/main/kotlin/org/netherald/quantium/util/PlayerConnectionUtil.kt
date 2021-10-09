package org.netherald.quantium.util

import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.netherald.quantium.data.ServerData

fun ProxiedPlayer.connect(servers : List<ServerInfo>, algorithm : PlayerConnectionUtil.SelectionAlgorithm) {
    PlayerConnectionUtil.connect(this, servers, algorithm)
}


object PlayerConnectionUtil {

    fun connectToLobby(player: ProxiedPlayer) {
        connect(player, ServerData.lobby, SelectionAlgorithm.PLAYER_COUNT_LOWER)
    }

    // server name
    fun connect(player: ProxiedPlayer, servers: List<ServerInfo>, algorithm: SelectionAlgorithm) {
        when (algorithm) {
            SelectionAlgorithm.RANDOM -> {
                player.connect(servers.random())
            }

            SelectionAlgorithm.PLAYER_COUNT_HIGHER -> {
                lateinit var nowServer : ServerInfo
                servers.forEach {
                    if (nowServer.players.size < it.players.size) {
                        nowServer = it
                    }
                }
                player.connect(nowServer)
            }

            SelectionAlgorithm.PLAYER_COUNT_LOWER -> {
                lateinit var nowServer : ServerInfo
                servers.forEach {

                    if (it.players.size < nowServer.players.size) {
                        nowServer = it
                    }
                }

                player.connect(nowServer)
            }
        }
    }

    enum class SelectionAlgorithm {
        RANDOM,
        PLAYER_COUNT_HIGHER,
        PLAYER_COUNT_LOWER
    }
}