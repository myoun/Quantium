package event

import org.bukkit.event.HandlerList
import org.netherald.quantium.MiniGameInstance

class InstanceStoppedEvent(override val instance: MiniGameInstance) : InstanceEvent() {
    companion object {
        @JvmStatic
        val handlerList =  HandlerList()
    }
    override fun getHandlers(): HandlerList = handlerList
}