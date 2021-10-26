package org.netherald.quantium.event

import org.bukkit.event.Event
import org.netherald.quantium.MiniGameInstance

abstract class InstanceEvent : Event() {
    abstract val instance : MiniGameInstance
}