package org.netherald.quantium

object Channels {

    const val mainChannel = "quantium:main"

    object SubChannels {

        object Bukkit {
            const val game = "game"
            const val lobby = "lobby"
        }

        object Bungee {
            const val lobbyConnection = "LobbyConnection"
            const val miniGameConnection = "MiniGameConnection"
        }

        const val getMiniGamePlayerCount = "PlayerCountRequest"
        const val getMiniGamePlayerCountResponse = "PlayerCountResponse"

    }
}