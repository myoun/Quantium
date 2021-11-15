package org.netherald.quantium

object ConfigPath {
    const val ENABLE = "enable"
    const val LOBBY = "lobby"
    const val MINI_GAME = "minigame"
    const val REDIS = "redis"

    const val QUEUE_SERVER = "queue-server"

    object Redis {
        const val address = "address"
        const val port = "port"
        const val password = "password"
    }
    object MiniGame {
        const val MIN_PLAYER_SIZE = "min-player-size"
        const val MAX_PLAYER_SIZE = "max-player-size"
        const val SERVERS = "servers"

        const val MAX_INSTANCE_SIZE = "max-instance-size"
    }
}