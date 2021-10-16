package org.netherald.quantium

import net.md_5.bungee.api.ReconnectHandler
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.netherald.quantium.data.PlayerData
import java.util.*
import kotlin.collections.HashMap

class MiniGameReConnectHandler : ReconnectHandler {

    // last MiniGame server connection
    val lastServer = HashMap<UUID, ServerInfo>()

    override fun getServer(player: ProxiedPlayer): ServerInfo? {
        return lastServer[player.uniqueId]
    }

    override fun setServer(player: ProxiedPlayer) {
        PlayerData.playerPlayingMap[player.uniqueId]?.let {
            lastServer[player.uniqueId] = player.server.info
        }
    }

    override fun save() {
    }

    override fun close() {
    }
}