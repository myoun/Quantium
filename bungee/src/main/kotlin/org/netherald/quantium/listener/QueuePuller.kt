package org.netherald.quantium.listener

import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import org.netherald.quantium.data.ServerData
import org.netherald.quantium.data.isLobby
import org.netherald.quantium.event.InstanceAddedEvent
import org.netherald.quantium.event.ServerLobbyAddedEvent
import org.netherald.quantium.event.ServerUnBlockedEvent

class QueuePuller : Listener {
    @EventHandler
    fun onMiniGameCreated(event : InstanceAddedEvent) {
        val miniGame = event.miniGame
        miniGame.poolPlayersInQueue(miniGame.maxPlayerSize).forEach {
            event.instance.addPlayer(it)
        }
    }

    @EventHandler
    fun onLobbyUnBlocked(event: ServerUnBlockedEvent) {
        if (event.serverInfo.isLobby) {
            ServerData.lobbyQueue.forEach { it.connect(event.serverInfo) }
        }
    }

    @EventHandler
    fun onServerLobbyAdded(event: ServerLobbyAddedEvent) {
        ServerData.lobbyQueue.forEach { it.connect(event.serverInfo) }
    }
}