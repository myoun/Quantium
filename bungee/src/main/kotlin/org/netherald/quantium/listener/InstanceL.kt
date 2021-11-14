package org.netherald.quantium.listener

import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import org.netherald.quantium.event.InstanceAddedEvent
import org.netherald.quantium.event.InstanceDeletedEvent

class InstanceL : Listener {
    @EventHandler
    fun onAdded(event : InstanceAddedEvent) {
        event.miniGame.apply { event.instance.server.countInstanceCount() }
    }

    @EventHandler
    fun onDeleted(event : InstanceDeletedEvent) {
        event.miniGame.apply { event.instance.server.discountInstanceCount() }
    }
}