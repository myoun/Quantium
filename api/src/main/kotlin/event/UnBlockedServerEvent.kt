package event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class UnBlockedServerEvent : Event() {
    companion object {
        @JvmStatic
        var handlerList = HandlerList()
    }
    override fun getHandlers(): HandlerList { return handlerList }
}