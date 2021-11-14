package org.netherald.quantium.listener

import com.google.common.io.ByteStreams
import io.lettuce.core.api.sync.multi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.netherald.quantium.MiniGameInstance
import org.netherald.quantium.RedisKeyType
import org.netherald.quantium.RedisMessageType
import org.netherald.quantium.event.InstanceCreatedEvent
import org.netherald.quantium.event.InstanceDeletedEvent
import org.netherald.quantium.event.InstanceStartedEvent
import org.netherald.quantium.event.InstanceStoppedEvent
import org.netherald.quantium.util.PluginMessageServerUtil
import org.netherald.quantium.util.RedisServerUtil

class InstanceDataL : Listener {

    private fun publish(instance : MiniGameInstance, channel : String, value : String) {
        RedisServerUtil.sync?.apply {
            publish(
                "${RedisKeyType.INSTANCE}:${instance.uuid}:${channel}",
                value
            )
        }
    }

    private fun serverPublish(channel : String, value : String) {
        RedisServerUtil.sync?.apply {
            publish(
                "${RedisKeyType.SERVER}:${RedisServerUtil.instance!!.serverName}:${channel}",
                value
            )
        }
    }

    @EventHandler
    fun onCreated(event : InstanceCreatedEvent) {
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
            serverPublish(RedisMessageType.ADDED_INSTANCE, event.instance.uuid.toString())
        } ?: run {
            @Suppress("UnstableApiUsage")
            val out = ByteStreams.newDataOutput()
            out.writeUTF(event.instance.miniGame.name)
            out.writeUTF(event.instance.uuid.toString())
            PluginMessageServerUtil.instance!!.sendPluginMessage(out.toByteArray())
        }
    }

    @EventHandler
    fun onDeleted(event : InstanceDeletedEvent) {
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
            serverPublish(RedisMessageType.DELETED_INSTANCE, event.instance.uuid.toString())
        } ?: run {
            @Suppress("UnstableApiUsage")
            val out = ByteStreams.newDataOutput()
            out.writeUTF(event.instance.uuid.toString())
            PluginMessageServerUtil.instance!!.sendPluginMessage(out.toByteArray())
        }
    }

    @EventHandler
    fun onStarted(event : InstanceStartedEvent) {
        RedisServerUtil.sync?.multi {
            sadd(RedisKeyType.INSTANCE_STARTED, event.instance.uuid.toString())
            publish(event.instance, RedisMessageType.STARTED_INSTANCE, event.instance.uuid.toString())
        } ?: run {
            TODO()
        }
    }

    @EventHandler
    fun onStopped(event : InstanceStoppedEvent) {
        RedisServerUtil.sync?.multi {
            sadd(RedisKeyType.INSTANCE_STOPPED, event.instance.uuid.toString())
            publish(event.instance, RedisMessageType.STOPPED_INSTANCE, event.instance.uuid.toString())
        } ?: run {
            TODO()
        }
    }
}