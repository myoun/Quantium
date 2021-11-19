package event

import org.bukkit.event.HandlerList
import org.netherald.quantium.MiniGameInstance

class InstanceThrownEvent(
    override val instance: MiniGameInstance,
    val throwable : Throwable,
) : InstanceEvent() {
    companion object { @JvmStatic val handlerList =  HandlerList() }
    override fun getHandlers(): HandlerList = handlerList
}