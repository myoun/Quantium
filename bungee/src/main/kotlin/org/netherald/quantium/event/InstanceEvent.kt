package org.netherald.quantium.event

import net.md_5.bungee.api.plugin.Event
import org.netherald.quantium.MiniGameInstance

abstract class InstanceEvent : Event() {
    abstract val instance : MiniGameInstance
}