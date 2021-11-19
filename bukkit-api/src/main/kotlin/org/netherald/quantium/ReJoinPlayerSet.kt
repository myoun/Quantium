package org.netherald.quantium

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.netherald.quantium.event.InstanceAddReJoinDataEvent
import org.netherald.quantium.event.InstanceRemoveReJoinDataEvent

class ReJoinPlayerSet(val instance : MiniGameInstance) : HashSet<Player>() {
    override fun add(element: Player): Boolean {
        if (!contains(element)) {
            Bukkit.getPluginManager().callEvent(InstanceAddReJoinDataEvent(instance, element))
        }
        return super.add(element)
    }

    override fun remove(element: Player): Boolean {
        if (contains(element)) {
            Bukkit.getPluginManager().callEvent(InstanceRemoveReJoinDataEvent(instance, element))
        }
        return super.remove(element)
    }
}