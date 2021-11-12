package org.netherald.quantium.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class UnBlockedServerEvent : Event() {
    companion object {
        @JvmStatic
        var handlerlist = HandlerList()
    }
    override fun getHandlers(): HandlerList { return handlerlist }
}