package org.netherald.quantium.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.netherald.quantium.RedisMessageType
import org.netherald.quantium.event.InstanceCreatedEvent
import org.netherald.quantium.event.InstanceDeletedEvent
import org.netherald.quantium.util.RedisServerUtil

class InstanceDataL : Listener {

    private fun publish(channel : String, value : String) {
        RedisServerUtil.instance?.apply {
            connection.sync().publish(
                "$serverName/${channel}", value
            )
        }
    }

    @EventHandler
    fun onCreated(event : InstanceCreatedEvent) {
        publish(RedisMessageType.ADDED_INSTANCE, event.instance.miniGame.name)
    }

    @EventHandler
    fun onDeleted(event : InstanceDeletedEvent) {
        publish(RedisMessageType.DELETED_INSTANCE, event.instance.miniGame.name)
    }
}