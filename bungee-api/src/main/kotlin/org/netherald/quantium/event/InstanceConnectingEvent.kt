package org.netherald.quantium.event

import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Cancellable
import net.md_5.bungee.api.plugin.Event
import org.netherald.quantium.MiniGameInfo
import org.netherald.quantium.MiniGameInstance

data class InstanceConnectingEvent(
    val player : ProxiedPlayer,
    val instance : MiniGameInstance,
) : Event(), Cancellable {

    private var isCancelled : Boolean = false
    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(cancel: Boolean) {
        isCancelled = cancel
    }
}