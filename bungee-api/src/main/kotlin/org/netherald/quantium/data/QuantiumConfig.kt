package org.netherald.quantium.data

import net.md_5.bungee.api.config.ServerInfo

object QuantiumConfig {
    var isDebug = false
    var queueServers = ArrayList<ServerInfo>()
    var isRedis : Boolean = false
}