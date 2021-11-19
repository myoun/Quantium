package org.netherald.eventlogger

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.netherald.quantium.event.InstanceCreatedEvent
import org.netherald.quantium.event.InstanceDeletedEvent
import org.netherald.quantium.event.MiniGameCreateEvent
import org.netherald.quantium.event.MiniGameDeletedEvent

class EventListener : Listener {
    @EventHandler
    fun onMiniGameCreated(event : MiniGameCreateEvent) {
        println("Created ${event.miniGame.name}")
    }

    @EventHandler
    fun onMiniGameDeleted(event : MiniGameDeletedEvent) {
        println("Deleted ${event.miniGame.name}")
    }

    @EventHandler
    fun onInstanceCreated(event: InstanceCreatedEvent) {
        println("${event.instance.miniGame.name}'s instance is created")
    }

    @EventHandler
    fun onInstanceDeleted(event: InstanceDeletedEvent) {
        println("${event.instance.miniGame.name}'s instance is deleted")
    }
}