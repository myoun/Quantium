package org.netherald.quantium.listener

import io.lettuce.core.api.sync.multi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.netherald.quantium.RedisKeyType
import org.netherald.quantium.RedisMessageType
import org.netherald.quantium.event.InstanceCreatedEvent
import org.netherald.quantium.event.InstanceDeletedEvent
import org.netherald.quantium.util.RedisServerUtil

class InstanceDataL : Listener {

    private fun publish(channel : String, value : String) {
        RedisServerUtil.sync?.apply {
            publish(
                "${RedisKeyType.SERVER}:${RedisServerUtil.instance!!.serverName}:${channel}", value
            )
        }
    }

    @EventHandler
    fun onCreated(event : InstanceCreatedEvent) {
        publish(RedisMessageType.ADDED_INSTANCE, event.instance.miniGame.name)
        RedisServerUtil.sync?.multi {
            sadd(
                "${RedisKeyType.SERVER}:${RedisServerUtil.instance!!.serverName}:${RedisKeyType.INSTANCES}",
                event.instance.uuid.toString()
            )
            sadd(
                "${RedisKeyType.MINI_GAME}:${event.instance.miniGame.name}:${RedisKeyType.INSTANCE}",
                event.instance.uuid.toString()
            )
            set(
                "${RedisKeyType.INSTANCE}:${event.instance.uuid}:${RedisKeyType.MINI_GAME}",
                event.instance.miniGame.name
            )
        }
    }

    @EventHandler
    fun onDeleted(event : InstanceDeletedEvent) {
        publish(RedisMessageType.DELETED_INSTANCE, event.instance.miniGame.name)
        RedisServerUtil.sync?.multi {
            srem(
                "${RedisKeyType.SERVER}:${RedisServerUtil.instance!!.serverName}:${RedisKeyType.INSTANCES}",
                event.instance.uuid.toString()
            )
            srem(
                "${RedisKeyType.MINI_GAME}:${event.instance.miniGame.name}:${RedisKeyType.INSTANCE}",
                event.instance.uuid.toString()
            )
            del("${RedisKeyType.INSTANCE}:${event.instance.uuid}:${RedisKeyType.MINI_GAME}")
        }
    }
}