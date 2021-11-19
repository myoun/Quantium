package org.netherald.quantium

object Channels {

    const val MAIN_CHANNEL = "quantium:main"

    object SubChannels {

        object Bukkit {
            const val SET_BLOCK = "block"
            const val MINI_GAME = "miniGame"
            const val INSTANCE = "instance"
            const val LOBBY = "lobby"
            const val ADDED_INSTANCE = "addedInstance"
            const val DELETED_INSTANCE = "deletedInstance"

            const val STARTED_INSTANCE = "startedInstance"
            const val STOPPED_INSTANCE = "stoppedInstance"

            const val ADDED_REJOIN_DATA = "addedReJoinData"
            const val REMOVED_REJOIN_DATA = "removedReJoinData"
        }

        object Bungee {
            const val LOBBY_CONNECTION = "LobbyConnection"
            const val MINI_GAME_CONNECTION = "MiniGameConnection"
        }

        const val GET_MINI_GAMES = "getMiniGames"
        const val GET_MINI_GAMES_RESPONSE = "getMiniGamesResponse"

        const val GET_INSTANCES = "getInstances"
        const val GET_INSTANCES_RESPONSE = "getInstancesResponse"

        const val GET_MINI_GAME_PLAYER_COUNT = "PlayerCountRequest"
        const val GET_MINI_GAME_PLAYER_COUNT_RESPONSE = "PlayerCountResponse"

    }
}
