package org.netherald.quantium.event

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.netherald.quantium.MiniGameInstance

class InstanceRemoveReJoinDataEvent(override val instance: MiniGameInstance, val player : Player) : InstanceEvent() {
    companion object { @JvmStatic val handlerList =  HandlerList() }
    override fun getHandlers(): HandlerList = handlerList
}