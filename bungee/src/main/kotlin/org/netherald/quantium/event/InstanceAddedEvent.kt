package org.netherald.quantium.event

import net.md_5.bungee.api.plugin.Event
import org.netherald.quantium.MiniGameInfo
import org.netherald.quantium.MiniGameInstance

class InstanceAddedEvent(override val instance : MiniGameInstance) : InstanceEvent() {
    val miniGame : MiniGameInfo get() = instance.miniGame
}