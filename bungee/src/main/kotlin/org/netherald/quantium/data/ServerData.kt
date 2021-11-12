package org.netherald.quantium.data

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import org.netherald.quantium.MiniGameInfo
import org.netherald.quantium.event.ServerBlockedEvent
import org.netherald.quantium.event.ServerUnBlockedEvent
import org.netherald.quantium.util.exception.NotFoundMiniGameException

object ServerData {
    val lobby = ArrayList<ServerInfo>()
    val miniGameServerData = HashMap<MiniGameInfo, Collection<ServerInfo>>()
    val blockedServers = HashSet<ServerInfo>()
    private val serverMiniGameData = HashMap<ServerInfo, ArrayList<MiniGameInfo>>()

    fun minigames(serverInfo: ServerInfo) : List<MiniGameInfo> {
        serverMiniGameData[serverInfo] ?: run {
            serverMiniGameData[serverInfo] = ArrayList()
        }
        return serverMiniGameData[serverInfo] as ArrayList<MiniGameInfo>
    }
}

val ServerInfo.minigames : List<MiniGameInfo>
    get() {
        return ServerData.minigames(this)
    }

fun ServerInfo.addMiniGame(name : String) {
    MiniGameData.addMiniGame(this, name)
}

fun ServerInfo.setLobby() {
    ServerData.lobby.add(this)
}

var ServerInfo.isBlocked : Boolean
    get() = ServerData.blockedServers.contains(this)
    set(value) {
        if (value) {
            ServerData.blockedServers += this
            ProxyServer.getInstance().pluginManager.callEvent(ServerBlockedEvent())
        } else {
            ServerData.blockedServers -= this
            ProxyServer.getInstance().pluginManager.callEvent(ServerUnBlockedEvent())
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
    miniGameInfo.runningInstanceCount[this]
        ?: run { (miniGameInfo.runningInstanceCount as MutableMap<ServerInfo, Int>)[this] = 0 }
    return miniGameInfo.runningInstanceCount[this]!!
}

fun ServerInfo.maxInstanceCount(miniGameInfo: MiniGameInfo) : Int {
    if (miniGameInfo.servers.contains(this)) {
        throw NotFoundMiniGameException()
    }
    miniGameInfo.maxInstanceCount[this]
        ?: run { (miniGameInfo.maxInstanceCount as MutableMap<ServerInfo, Int>)[this] = 0 }
    return miniGameInfo.maxInstanceCount[this]!!
}