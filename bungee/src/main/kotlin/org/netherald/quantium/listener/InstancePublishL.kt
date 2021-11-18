package org.netherald.quantium.listener

import io.lettuce.core.pubsub.RedisPubSubAdapter
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Event
import org.netherald.quantium.MiniGameInstance
import org.netherald.quantium.RedisKeyType
import org.netherald.quantium.RedisMessageType
import org.netherald.quantium.data.PlayerData
import org.netherald.quantium.event.InstanceReJoinDataAddedEvent
import org.netherald.quantium.event.InstanceReJoinDataRemovedEvent
import org.netherald.quantium.event.InstanceStartedEvent
import org.netherald.quantium.event.InstanceStoppedEvent
import java.util.*

class InstancePublishL(
    val instance : MiniGameInstance
) : RedisPubSubAdapter<String, String>() {
    override fun message(channel: String, message: String) {
        val callEvent = fun (event : Event) = ProxyServer.getInstance().pluginManager.callEvent(event)
        if (RedisKeyType.INSTANCE != channel.split(":")[0] ||
            instance.uuid.toString() != channel.split(":")[1]) return
        when (channel.split(":")[2]) {
            RedisKeyType.INSTANCE_STARTED -> {
                instance.isStarted = true
                callEvent(InstanceStartedEvent(instance))
            }
            RedisKeyType.INSTANCE_STOPPED -> {
                instance.isStopped = true
                callEvent(InstanceStoppedEvent(instance))
            }

            RedisMessageType.REJOIN_DATA_ADD -> {
                val uuid = UUID.fromString(message)
                PlayerData.playerReJoinInstance[uuid] = instance
                instance.reJoinData += uuid
                callEvent(InstanceReJoinDataAddedEvent(instance, uuid))
            }

            RedisMessageType.REJOIN_DATA_REMOVE -> {
                val uuid = UUID.fromString(message)
                PlayerData.playerReJoinInstance.remove(uuid, instance)
                instance.reJoinData -= uuid
                callEvent(InstanceReJoinDataRemovedEvent(instance, uuid))
            }
        }
    }
}