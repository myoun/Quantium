package event

import org.bukkit.event.HandlerList
import org.netherald.quantium.MiniGame

class MiniGameThrownEvent(
    override val miniGame: MiniGame,
    val throwable : Throwable
) : MiniGameEvent() {
    companion object { @JvmStatic val handlerList =  HandlerList() }
    override fun getHandlers(): HandlerList = handlerList
}