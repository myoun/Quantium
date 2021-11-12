package org.netherald.quantium.util

import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Event
import org.netherald.quantium.RedisMessageType
import org.netherald.quantium.data.MiniGameData
import org.netherald.quantium.data.isBlocked
import org.netherald.quantium.event.InstanceAddedEvent
import org.netherald.quantium.event.InstanceDeletedEvent

object RedisServerUtil {

    lateinit var client : RedisClient
    lateinit var connection: StatefulRedisConnection<String, String>

    private var blocked : Boolean = false
    val isBlocked: Boolean get() = blocked

    fun init(redisURI: RedisURI) {

        client = RedisClient.create(redisURI)!!
        connection = client.connect()

        val callEvent = fun (event : Event) = ProxyServer.getInstance().pluginManager.callEvent(event)
        connection.addListener { message ->
            val server = ProxyServer.getInstance().getServerInfo(message.type.split("/")[0])
            when (message.type.split("/")[1]) {
                RedisMessageType.BLOCK -> {
                    message.content[0]?.let {
                        it as String
                        server.isBlocked = it.toBoolean()
                    }
                }
                RedisMessageType.ADDED_INSTANCE -> {
                    message.content[0]?.let {
                        callEvent(InstanceAddedEvent(server, MiniGameData.minigames[it as String]!!))
                    }
                }

                RedisMessageType.DELETED_INSTANCE -> {
                    message.content[0]?.let {
                        callEvent(InstanceDeletedEvent(server, MiniGameData.minigames[it as String]!!))
                    }
                }
            }
        }
        val sync = client.connectPubSub().sync()
        ProxyServer.getInstance().servers.forEach { (name, _) ->
            sync.subscribe("$name/${RedisMessageType.BLOCK}")
            sync.subscribe("$name/${RedisMessageType.ADDED_INSTANCE}")
            sync.subscribe("$name/${RedisMessageType.DELETED_INSTANCE}")
        }

    }
}