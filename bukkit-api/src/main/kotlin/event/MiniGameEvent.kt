package event

import org.bukkit.event.Event
import org.netherald.quantium.MiniGame

abstract class MiniGameEvent() : Event() {
    abstract val miniGame : MiniGame
}