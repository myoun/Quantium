package org.netherald.quantium.listener

import io.lettuce.core.pubsub.RedisPubSubAdapter
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Event
import org.netherald.quantium.MiniGameInstance
import org.netherald.quantium.RedisKeyType
import org.netherald.quantium.RedisMessageType
import org.netherald.quantium.data.MiniGameData
import org.netherald.quantium.data.isBlocked
import org.netherald.quantium.event.*
import org.netherald.quantium.util.RedisServerUtil
import java.util.*

class ServerPublishL : RedisPubSubAdapter<String, String>() {
    override fun message(channel: String, message: String) {
        if (channel.split(":")[0] != RedisKeyType.SERVER) return

        val server = ProxyServer.getInstance().getServerInfo(channel.split(":")[1])

        val callEvent = fun (event : Event) = ProxyServer.getInstance().pluginManager.callEvent(event)

        when (channel.split(":")[2]) {
            RedisMessageType.BLOCK -> {
                server.isBlocked = message.toBoolean()
                if (server.isBlocked) {
                    callEvent(ServerBlockedEvent(server))
                } else {
                    callEvent(ServerUnBlockedEvent(server))
                }
            }
            RedisMessageType.ADDED_INSTANCE -> {
                val uuid = UUID.fromString(message)
                val instance = MiniGameInstance(
                    uuid,
                    server,
                    MiniGameData.instances[uuid]!!.miniGame
                )

                val connection = RedisServerUtil.client!!.connectPubSub()
                connection.addListener(InstancePublishL(instance))

                connection.sync().subscribe("${RedisKeyType.INSTANCE}:$uuid:${RedisKeyType.INSTANCE_STARTED}")
                connection.sync().subscribe("${RedisKeyType.INSTANCE}:$uuid:${RedisKeyType.INSTANCE_STOPPED}")
                RedisServerUtil.instanceConnection[instance] = connection
                callEvent(InstanceAddedEvent(instance))
            }

            RedisMessageType.DELETED_INSTANCE -> {
                val uuid = UUID.fromString(message)
                val instance = MiniGameData.instances[uuid]!!
                instance.delete()
                callEvent(InstanceDeletedEvent(MiniGameData.instances[uuid]!!))
                RedisServerUtil.instanceConnection[instance]!!.closeAsync()
            }

            RedisMessageType.MINI_GAME_ADDED -> {
                val uuid = UUID.fromString(message)
                val instance = MiniGameData.instances[uuid]!!
                instance.delete()
                callEvent(
                    InstanceDeletedEvent(MiniGameData.instances[uuid]!!)
                )
                RedisServerUtil.instanceConnection[instance]!!.closeAsync()
            }

            RedisMessageType.MINI_GAME_REMOVED -> {
                val uuid = UUID.fromString(message)
                val instance = MiniGameData.instances[uuid]!!
                instance.delete()
                callEvent(
                    InstanceDeletedEvent(MiniGameData.instances[uuid]!!)
                )
                RedisServerUtil.instanceConnection[instance]!!.closeAsync()
            }
        }
    }
}