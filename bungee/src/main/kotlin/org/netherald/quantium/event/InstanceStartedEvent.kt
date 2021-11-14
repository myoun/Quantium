package org.netherald.quantium.event

import org.netherald.quantium.MiniGameInstance

class InstanceStartedEvent(override val instance: MiniGameInstance) : InstanceEvent() {
}