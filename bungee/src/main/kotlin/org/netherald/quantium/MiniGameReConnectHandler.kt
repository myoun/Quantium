package org.netherald.quantium

import net.md_5.bungee.api.ReconnectHandler
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.netherald.quantium.data.*
import org.netherald.quantium.util.PlayerConnectionUtil
import org.netherald.quantium.util.bestServer

class MiniGameReConnectHandler : ReconnectHandler {

    override fun getServer(player: ProxiedPlayer): ServerInfo? {
        player.reJoinInstance?.server?.let {
            return it
        } ?: run {
            val servers = ServerData.lobbies.filter { !it.isBlocked }
            if (servers.isEmpty()) {
                return QuantiumConfig.queueServers.bestServer
            }
             return PlayerConnectionUtil.getServer(
                 servers,
                 PlayerConnectionUtil.SelectionAlgorithm.PLAYER_COUNT_LOWER
             )
        }
    }

    override fun setServer(player: ProxiedPlayer) {}

    override fun save() {}

    override fun close() {}

}