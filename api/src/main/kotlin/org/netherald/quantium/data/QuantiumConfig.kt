package org.netherald.quantium.data

import org.bukkit.Location

object QuantiumConfig {

    var enableLobby : Boolean = false
    var enableMiniGame : Boolean = false
    lateinit var lobbyLocation : Location

    object Bungee {
        var enable: Boolean = false
        lateinit var serverName: String
    }

    object Redis {
        var enable : Boolean = false
        lateinit var password : String
        lateinit var address: String
        var port: Int = 0
    }
}