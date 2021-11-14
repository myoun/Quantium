package org.netherald.quantium.event

import org.netherald.quantium.MiniGameInstance

class InstanceStoppedEvent(override val instance: MiniGameInstance) : InstanceEvent() {
}