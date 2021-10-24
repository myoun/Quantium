package org.netherald.quantium.data

import org.bukkit.entity.Player
import org.netherald.quantium.MiniGameInstance

object PlayerData {
    val connectionType = HashMap<Player, ConnectionType>()
    val playingGame = HashMap<Player, MiniGameInstance>()
    val playerReJoinData = HashMap<Player, MiniGameInstance>()

    object UnSafe {
        fun setConnectionType(player: Player, value: ConnectionType) {
            connectionType[player] = value
        }
        fun setPlayingGame(player: Player, value : MiniGameInstance) {
            playingGame[player]?.removePlayer(player)
            playingGame[player] = value
        }
        fun setReJoinData(player: Player, value : MiniGameInstance) {
            playerReJoinData[player]?.reJoinData?.remove(player)
            playerReJoinData[player] = value
        }

        fun addAllMiniGameData(player: Player, miniGameInstance: MiniGameInstance) {

            player.clearData()
            connectionType[player] = ConnectionType.MINIGAME
            playingGame[player] = miniGameInstance
            playerReJoinData[player] = miniGameInstance

            miniGameInstance.miniGame.players as MutableList
            miniGameInstance.miniGame.players.add(player)
            miniGameInstance.players.add(player)

        }

        fun clearData(player: Player) {
            player.clearData()
        }
    }
}

val Player.connectionType : ConnectionType?
    get() = PlayerData.connectionType[this]

val Player.playingGame : MiniGameInstance?
    get() = PlayerData.playingGame[this]

var Player.reJoinData : MiniGameInstance?
    get() = PlayerData.playerReJoinData[this]
    set(value) {
        value?.let {
            playingGame?.reJoinData?.remove(this)
            PlayerData.playerReJoinData[this] = value
        } ?: run {
            playingGame?.reJoinData?.remove(this)
            PlayerData.playerReJoinData.remove(this)
        }
    }

private fun Player.clearData() {
    clearConnectionType()
    clearReJoinData()
    clearPlayingGameData()
}

private fun Player.clearPlayingGameData() {
    playingGame?.let {
        (it.miniGame.players as MutableList<Player>).add(this)
        it.players.remove(player)
    }
    PlayerData.playingGame.remove(this)
}

fun Player.clearReJoinData() {
    reJoinData = null
}

private fun Player.clearConnectionType() {
    PlayerData.connectionType.remove(this)
}

enum class ConnectionType {
    MINIGAME,
    LOBBY
}