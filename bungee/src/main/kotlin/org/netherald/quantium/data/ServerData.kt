package org.netherald.quantium.data

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import org.netherald.quantium.ConfigPath
import org.netherald.quantium.MiniGameInfo
import org.netherald.quantium.event.ServerBlockedEvent
import org.netherald.quantium.event.ServerUnBlockedEvent
import org.netherald.quantium.util.RedisServerUtil
import org.netherald.quantium.exception.NotFoundMiniGameException

object ServerData {
    val lobby = ArrayList<ServerInfo>()
    val blockedServers = HashSet<ServerInfo>()
    private val serverMiniGameData = HashMap<ServerInfo, HashSet<MiniGameInfo>>()

    fun miniGames(serverInfo: ServerInfo) : Collection<MiniGameInfo> {
        serverMiniGameData[serverInfo] ?: run {
            serverMiniGameData[serverInfo] = HashSet()
        }
        return serverMiniGameData[serverInfo] as HashSet<MiniGameInfo>
    }

    fun addMiniGame(serverInfo: ServerInfo, game : MiniGameInfo) {
        (game.servers as HashSet<ServerInfo>).add(serverInfo)
        (serverMiniGameData[serverInfo] as HashSet<MiniGameInfo>).add(game)
    }

    fun addMiniGame(serverInfo: ServerInfo, name: String) {
        addMiniGame(serverInfo, MiniGameData.miniGames[name]!!)
    }

    fun removeMiniGame(serverInfo: ServerInfo, name : String) {
        removeMiniGame(serverInfo, MiniGameData.miniGames[name]!!)
    }

    fun removeMiniGame(serverInfo: ServerInfo, game : MiniGameInfo) {
        (game.servers as HashSet<ServerInfo>).remove(serverInfo)
        (serverMiniGameData[serverInfo] as HashSet<MiniGameInfo>).remove(game)
    }
}

val ServerInfo.miniGames : Collection<MiniGameInfo> get() { return ServerData.miniGames(this) }

fun ServerInfo.addMiniGameServer(game : MiniGameInfo, maxInstanceCount : Int) {
    ServerData.addMiniGame(this, game)
    RedisServerUtil.addMiniGameServer(this.name, game.name)
    (game.maxInstanceCount as MutableMap<ServerInfo, Int>)[this] = maxInstanceCount
}

fun ServerInfo.setLobby() {
    ServerData.lobby.add(this)
    RedisServerUtil.addLobby(this)
}

var ServerInfo.isBlocked : Boolean
    get() = ServerData.blockedServers.contains(this)
    set(value) {
        if (value) {
            ServerData.blockedServers += this
            ProxyServer.getInstance().pluginManager.callEvent(ServerBlockedEvent(this))
        } else {
            ServerData.blockedServers -= this
            ProxyServer.getInstance().pluginManager.callEvent(ServerUnBlockedEvent(this))
        }
    }

fun ServerInfo.playerCount(miniGameInfo: MiniGameInfo) : Int {
    if (miniGameInfo.servers.contains(this)) {
        throw NotFoundMiniGameException()
    }
    miniGameInfo.playerCount[this] ?: run { (miniGameInfo.playerCount as MutableMap<ServerInfo, Int>)[this] = 0 }
    return miniGameInfo.playerCount[this]!!
}

fun ServerInfo.instanceCount(miniGameInfo: MiniGameInfo) : Int {
    if (miniGameInfo.servers.contains(this)) {
        throw NotFoundMiniGameException()
    }
    miniGameInfo.startedInstanceCount[this]
        ?: run { (miniGameInfo.startedInstanceCount as MutableMap<ServerInfo, Int>)[this] = 0 }
    return miniGameInfo.startedInstanceCount[this]!!
}

fun ServerInfo.maxInstanceCount(miniGameInfo: MiniGameInfo) : Int {
    if (miniGameInfo.servers.contains(this)) {
        throw NotFoundMiniGameException()
    }
    miniGameInfo.maxInstanceCount[this]
        ?: run { (miniGameInfo.maxInstanceCount as MutableMap<ServerInfo, Int>)[this] = 0 }
    return miniGameInfo.maxInstanceCount[this]!!
}
