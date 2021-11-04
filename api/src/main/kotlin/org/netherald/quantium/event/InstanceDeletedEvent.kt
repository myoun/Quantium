package org.netherald.quantium.event

import org.bukkit.event.HandlerList
import org.netherald.quantium.MiniGameInstance

class InstanceDeletedEvent(override val instance: MiniGameInstance) : InstanceEvent() {
    companion object {
        @JvmStatic
        var handlerlist = HandlerList()
    }
    override fun getHandlers(): HandlerList { return handlerlist }
}