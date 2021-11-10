package org.netherald.quantium.event

import org.bukkit.event.HandlerList
import org.netherald.quantium.MiniGame

class MiniGameDeletedEvent(override val miniGame: MiniGame) : MiniGameEvent() {
    companion object {
        @JvmStatic
        var handlerlist = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return handlerlist
    }
}