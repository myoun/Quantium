package org.netherald.quantium.dataclass

import org.bukkit.entity.Player
import org.netherald.quantium.MiniGameInstance

val Player.connectionType : ConnectionType?
    get() = PlayerData.connectionType[this]

object PlayerData {
    val connectionType = HashMap<Player, ConnectionType>()
    val playerReJoinData = HashMap<Player, MiniGameInstance>()
}

val Player.reJoinData : MiniGameInstance?
    get() {
        return PlayerData.playerReJoinData[this]
    }

fun Player.clearReJoinData() {
    PlayerData.playerReJoinData.remove(this)
}

enum class ConnectionType {
    MINIGAME,
    LOBBY
}