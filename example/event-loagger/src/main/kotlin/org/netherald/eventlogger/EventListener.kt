package org.netherald.eventlogger

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import event.InstanceCreatedEvent
import event.InstanceDeletedEvent
import event.MiniGameCreateEvent
import event.MiniGameDeletedEvent

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