package org.netherald.quantium.util

import net.md_5.bungee.api.config.ServerInfo

fun addServer(server : ServerInfo) {
    ServerUtil.default.addServer(server)
}

fun addLobby(serverInfo : ServerInfo) {
    ServerUtil.default.addLobby(serverInfo)
}

fun removeLobby(serverInfo: ServerInfo) {
    ServerUtil.default.removeLobby(serverInfo)
}

fun addMiniGame(name : String) {
    ServerUtil.default.addMiniGame(name)
}

fun removeMiniGame(name : String) {
    ServerUtil.default.removeMiniGame(name)
}

fun addMiniGameServer(serverName: String, gameName: String) {
    ServerUtil.default.addMiniGameServer(serverName, gameName)
}

fun removeMiniGameServer(serverName: String, gameName: String) {
    ServerUtil.default.removeMiniGameServer(serverName, gameName)
}

interface ServerUtil {

    companion object {
        lateinit var default : ServerUtil
    }

    fun addServer(server : ServerInfo)

    fun addLobby(serverInfo : ServerInfo)

    fun removeLobby(serverInfo: ServerInfo)

    fun addMiniGame(name : String)

    fun removeMiniGame(name : String)

    fun addMiniGameServer(serverName: String, gameName: String)

    fun removeMiniGameServer(serverName: String, gameName: String)
}