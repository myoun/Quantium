package org.netherald.quantium.util

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.netherald.quantium.MiniGameInfo
import org.netherald.quantium.data.QuantiumConfig
import org.netherald.quantium.data.ServerData
import org.netherald.quantium.data.isBlocked
import org.netherald.quantium.debug
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

val Collection<ServerInfo>.bestServer : ServerInfo?
get() = PlayerConnectionUtil.getServer(this, PlayerConnectionUtil.SelectionAlgorithm.PLAYER_COUNT_LOWER)

object PlayerConnectionUtil {

    fun connectToLobby(player: ProxiedPlayer) {
        debug("connect to lobby ${player.name}")
        val lobbies = ServerData.lobbies.filter { !it.isBlocked }
        debug("Is empty : ${lobbies.isEmpty()}")
        if (lobbies.isEmpty()) {
            ServerData.lobbyQueue.add(player)
            player.connect(QuantiumConfig.queueServers.bestServer)
            return
        } else {
            connect(player, lobbies, SelectionAlgorithm.PLAYER_COUNT_LOWER)
        }
    }

    fun connectToGame(player: ProxiedPlayer, game : MiniGameInfo) {
        if (
            !ProxyServer.getInstance().pluginManager.callEvent(
                MiniGameConnectingEvent(player, game)
            ).isCancelled
        ) {
            game.addPlayer(player)
        }
    }

    // server name
    fun connect(
        player: ProxiedPlayer,
        servers: List<ServerInfo>,
        algorithm: SelectionAlgorithm
    ) = player.connect(getServer(servers, algorithm))

    fun getServer(servers: Collection<ServerInfo>, algorithm: SelectionAlgorithm) : ServerInfo? {
        if (servers.isEmpty()) return null
        when (algorithm) {
            SelectionAlgorithm.RANDOM -> {
                return servers.random()
            }

            SelectionAlgorithm.PLAYER_COUNT_HIGHER -> {
                var nowServer : ServerInfo? = null
                servers.forEach {
                    nowServer ?: kotlin.run { nowServer = it; return@forEach }
                    if (nowServer!!.players.size < it.players.size) {
                        nowServer = it
                    }
                }
                return nowServer!!
            }

            SelectionAlgorithm.PLAYER_COUNT_LOWER -> {
                var nowServer : ServerInfo? = null
                servers.forEach {
                    nowServer ?: run { nowServer = it; return@forEach }
                    if (it.players.size < nowServer!!.players.size) {
                        nowServer = it
                    }
                }

                return nowServer!!
            }
        }
    }

    enum class SelectionAlgorithm {
        RANDOM,
        PLAYER_COUNT_HIGHER,
        PLAYER_COUNT_LOWER
    }
}