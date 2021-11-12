package org.netherald.quantium.data

import net.md_5.bungee.api.config.ServerInfo
import org.netherald.quantium.MiniGameInfo
import org.netherald.quantium.data.ServerData.miniGameServerData

object MiniGameData {

    val minigames = HashMap<String, MiniGameInfo>()

    fun addMiniGameInfo(name: String) {
        minigames[name] = MiniGameInfo(name)
        miniGameServerData[minigames[name]!!] = ArrayList()
    }

    fun addMiniGame(serverInfo: ServerInfo, name: String) {
        if (minigames[name] == null) {
            addMiniGameInfo(name)
        }
        (miniGameServerData[minigames[name]!!] as MutableCollection).add(serverInfo)
    }

    fun removeMiniGame(name : String) {
        miniGameServerData.remove(minigames[name])
        minigames.remove(name)
    }

    fun removeMiniGame(miniGameInfo: MiniGameInfo) {
        miniGameServerData.remove(miniGameInfo)
        minigames.remove(miniGameInfo.name)
    }


    fun servers(miniGameInfo : MiniGameInfo) : Collection<ServerInfo> {
        miniGameServerData[miniGameInfo] ?: run {
            miniGameServerData[miniGameInfo] = HashSet()
        }
        return miniGameServerData[miniGameInfo]!!
    }
}


val MiniGameInfo.servers : Collection<ServerInfo>
    get() {
        return MiniGameData.servers(this)
    }