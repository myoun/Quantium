package org.netherald.quantium.event

import org.bukkit.event.HandlerList
import org.netherald.quantium.MiniGameInstance

class InstanceCreatedEvent(override val instance: MiniGameInstance) : InstanceEvent() {
    private val handlers = HandlerList()
    override fun getHandlers(): HandlerList { return handlers }
}