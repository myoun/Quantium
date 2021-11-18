package org.netherald.quantium.event

import org.netherald.quantium.MiniGameInstance
import java.util.*

class InstanceReJoinDataRemovedEvent(
    override val instance: MiniGameInstance,
    val playerUUID : UUID
) : InstanceEvent()