package org.netherald.quantium

object ConfigPath {
    const val ENABLE_LOBBY = "enable-lobby"
    const val ENABLE_MINIGAME = "enable-minigame"
    const val LOBBY_LOCATION = "lobby-location"
    const val REDIS = "redis"
    const val BUNGEECORD = "bungeecord"
    const val ENABLE = "enable"
    object Redis {
        const val PASSWORD = "password"
        const val ADDRESS = "address"
        const val PORT = "port"
    }
    object Bungee {
        const val SERVER_NAME = "server-name"
    }
}