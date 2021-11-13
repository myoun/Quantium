package org.netherald.quantium

object Channels {

    const val MAIN_CHANNEL = "quantium:main"

    object SubChannels {

        object Bukkit {
            const val GAME = "game"
            const val LOBBY = "lobby"
        }

        object Bungee {
            const val LOBBY_CONNECTION = "LobbyConnection"
            const val MINI_GAME_CONNECTION = "MiniGameConnection"
        }

        const val GET_MINI_GAMES = "getMiniGames"
        const val GET_MINI_GAMES_RESPONSE = "getMiniGamesResponse"

        const val GET_MINI_GAME_PLAYER_COUNT = "PlayerCountRequest"
        const val GET_MINI_GAME_PLAYER_COUNT_RESPONSE = "PlayerCountResponse"

    }
}