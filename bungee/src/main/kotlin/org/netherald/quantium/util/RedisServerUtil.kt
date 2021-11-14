package org.netherald.quantium.util

import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.multi
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.plugin.Event
import org.netherald.quantium.MiniGameInstance
import org.netherald.quantium.RedisKeyType
import org.netherald.quantium.RedisMessageType
import org.netherald.quantium.data.MiniGameData
import org.netherald.quantium.data.isBlocked
import org.netherald.quantium.event.InstanceAddedEvent
import org.netherald.quantium.event.InstanceDeletedEvent
import org.netherald.quantium.event.ServerBlockedEvent
import org.netherald.quantium.event.ServerUnBlockedEvent
import java.util.*

object RedisServerUtil {

    var client : RedisClient? = null
    var connection: StatefulRedisConnection<String, String>? = null

    fun init(redisURI: RedisURI) {

        client = RedisClient.create(redisURI)!!
        connection = client!!.connect()

        val callEvent = fun (event : Event) = ProxyServer.getInstance().pluginManager.callEvent(event)
        client!!.connectPubSub().addListener { message ->

            if (message.type.split(":")[0] != RedisKeyType.SERVER) return@addListener

            val server = ProxyServer.getInstance().getServerInfo(message.type.split(":")[1])
            when (message.type.split(":")[1]) {
                RedisMessageType.BLOCK -> {
                    message.content[0]?.let {
                        it as String
                        server.isBlocked = it.toBoolean()
                        if (server.isBlocked) {
                            callEvent(ServerBlockedEvent(server))
                        } else {
                            callEvent(ServerUnBlockedEvent(server))
                        }
                    }
                }
                RedisMessageType.ADDED_INSTANCE -> {
                    message.content[0]?.let {
                        val uuid = UUID.fromString(message.content[1] as String)
                        val instance = MiniGameInstance(
                            uuid,
                            server,
                            MiniGameData.getMiniGameType(uuid)!!
                        )
                        callEvent(
                            InstanceAddedEvent(instance)
                        )
                    }
                }

                RedisMessageType.DELETED_INSTANCE -> {
                    message.content[0]?.let {
                        val uuid = UUID.fromString(message.content[1] as String)
                        val instance = MiniGameData.instances[uuid]!!
                        instance.delete()
                        callEvent(
                            InstanceDeletedEvent(MiniGameData.instances[uuid]!!)
                        )
                    }
                }
            }
        }

        val sync = client!!.connectPubSub().sync()
        ProxyServer.getInstance().servers.forEach { (name, _) ->
            sync.subscribe("${RedisKeyType.SERVER}:$name:${RedisMessageType.BLOCK}")
            sync.subscribe("${RedisKeyType.SERVER}:$name:${RedisMessageType.ADDED_INSTANCE}")
            sync.subscribe("${RedisKeyType.SERVER}:$name:${RedisMessageType.DELETED_INSTANCE}")
        }
    }

    fun addLobby(serverInfo : ServerInfo) {
        val sync = connection?.sync()
        sync?.sadd(RedisKeyType.LOBBIES, serverInfo.name)
    }

    fun removeLobby(serverInfo: ServerInfo) {
        val sync = connection?.sync()
        sync?.srem(RedisKeyType.LOBBIES, serverInfo.name)
    }

    fun addMiniGame(name : String) {
        val sync = connection?.sync()
        sync?.sadd(RedisKeyType.MINI_GAMES, name)
    }

    fun removeMiniGame(name : String) {
        val sync = connection?.sync()
        sync?.multi {
            del("${RedisKeyType.MINI_GAME}:$name:${RedisKeyType.SERVERS}")
            srem(RedisKeyType.MINI_GAMES, name)
        }
    }

    fun addMiniGameServer(serverName: String, gameName: String) {
        val sync = connection?.sync()
        sync?.multi {
            sadd(RedisKeyType.MINI_GAMES, gameName)
            set("${RedisKeyType.SERVER}:$serverName:${RedisKeyType.MINI_GAMES}", gameName)
            sadd("${RedisKeyType.MINI_GAME}:${gameName}:${RedisKeyType.SERVERS}", serverName)
        }
    }

    fun removeMiniGameServer(serverName: String, gameName: String) {
        val sync = connection?.sync()
        sync?.multi {
            del("${RedisKeyType.SERVER}:$serverName:${RedisKeyType.MINI_GAMES}")
            srem("${RedisKeyType.MINI_GAME}:${gameName}:${RedisKeyType.SERVERS}", serverName)
        }
    }
}