package org.netherald.quantium.event

import org.netherald.quantium.MiniGameInfo
import org.netherald.quantium.MiniGameInstance

class InstanceDeletedEvent(override val instance : MiniGameInstance) : InstanceEvent() {
    val miniGame : MiniGameInfo get() = instance.miniGame
}