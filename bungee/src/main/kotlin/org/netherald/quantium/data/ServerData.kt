package org.netherald.quantium.data

import net.md_5.bungee.api.config.ServerInfo
import org.netherald.quantium.MiniGameInfo

object ServerData {
    val lobby = ArrayList<ServerInfo>()
    val miniGameServerData = HashMap<MiniGameInfo, ArrayList<ServerInfo>>()
    val serverMiniGameData = HashMap<ServerInfo, ArrayList<MiniGameInfo>>()

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