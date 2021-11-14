package org.netherald.quantium.util

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.netherald.quantium.MiniGameInfo
import org.netherald.quantium.data.ServerData
import org.netherald.quantium.data.isBlocked
import org.netherald.quantium.event.MiniGameConnectedEvent
import org.netherald.quantium.event.MiniGameConnectingEvent

fun ProxiedPlayer.connect(servers : List<ServerInfo>, algorithm : PlayerConnectionUtil.SelectionAlgorithm) {
    PlayerConnectionUtil.connect(this, servers, algorithm)
}

fun ProxiedPlayer.connectToLobby() {
    PlayerConnectionUtil.connectToLobby(this)
}

fun ProxiedPlayer.connectToGame(miniGameInfo: MiniGameInfo) {
    PlayerConnectionUtil.connectToGame(this, miniGameInfo)
}

object PlayerConnectionUtil {

    fun connectToLobby(player: ProxiedPlayer) {
        connect(player, ServerData.lobby.filter { !it.isBlocked }, SelectionAlgorithm.PLAYER_COUNT_LOWER)
    }

    fun connectToGame(player: ProxiedPlayer, game : MiniGameInfo) {
        if (
            !ProxyServer.getInstance().pluginManager.callEvent(
                MiniGameConnectingEvent(player, game)
            ).isCancelled
        ) {
            game.recommendMatchingInstance.addPlayer(player)
            ProxyServer.getInstance().pluginManager.callEvent(MiniGameConnectedEvent(player, game))
        }
    }

    // server name
    fun connect(player: ProxiedPlayer, servers: List<ServerInfo>, algorithm: SelectionAlgorithm) {
        when (algorithm) {
            SelectionAlgorithm.RANDOM -> {
                player.connect(servers.random())
            }

            SelectionAlgorithm.PLAYER_COUNT_HIGHER -> {
                lateinit var nowServer : ServerInfo
                servers.filter { !it.isBlocked }.forEach {
                    if (nowServer.players.size < it.players.size) {
                        nowServer = it
                    }
                }
                player.connect(nowServer)
            }

            SelectionAlgorithm.PLAYER_COUNT_LOWER -> {
                lateinit var nowServer : ServerInfo
                servers.filter { !it.isBlocked }.forEach {
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