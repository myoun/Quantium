package event

import org.bukkit.event.HandlerList
import org.netherald.quantium.MiniGame

class MiniGameCreateEvent(override val miniGame: MiniGame) : MiniGameEvent() {
    companion object {
        @JvmStatic
        var handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }
}