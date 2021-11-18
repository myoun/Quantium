package org.netherald.quantium.event

import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Cancellable
import net.md_5.bungee.api.plugin.Event
import org.netherald.quantium.MiniGameInfo

class PlayerJoinQueueEvent(
    val player : ProxiedPlayer,
    val miniGame : MiniGameInfo
) : Event(), Cancellable {
    private var cancelled = false
    override fun isCancelled(): Boolean = cancelled
    override fun setCancelled(cancel: Boolean) { cancelled = cancel }
}