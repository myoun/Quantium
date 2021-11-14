package org.netherald.quantium.event

import org.bukkit.event.HandlerList
import org.netherald.quantium.MiniGameInstance

class InstanceStartedEvent(override val instance: MiniGameInstance) : InstanceEvent() {
    companion object {
        @JvmStatic
        val handlerList =  HandlerList()
    }
    override fun getHandlers(): HandlerList = handlerList
}