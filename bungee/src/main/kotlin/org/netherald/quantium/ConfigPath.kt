package org.netherald.quantium

object ConfigPath {
    const val enable = "enable"
    const val lobby = "lobby"
    const val minigame = "minigame"
    const val redis = "redis"
    object Redis {
        const val address = "address"
        const val port = "port"
        const val password = "password"
    }
}