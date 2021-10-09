package org.netherald.quantium

object Channels {
    const val mainChannel = "org.netherald.quantium:main"
    object SubChannels {
        const val game = "game"
        const val lobby = "lobby"
        const val getMiniGamePlayerCount = "PlayerCountRequest"
        const val getMiniGamePlayerCountResponse = "PlayerCountResponse"

        const val lobbyConnection = "LobbyConnection"
        const val miniGameConnection = "MiniGameConnection"
    }
}