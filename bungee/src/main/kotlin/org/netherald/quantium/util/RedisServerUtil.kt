package org.netherald.quantium.util

import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import io.lettuce.core.api.sync.multi
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import org.netherald.quantium.MiniGameInstance
import org.netherald.quantium.RedisKeyType
import org.netherald.quantium.RedisMessageType
import org.netherald.quantium.listener.ServerPublishL
import java.util.*

class RedisServerUtil : ServerUtil {

    companion object {

        var client : RedisClient? = null
        var connection: StatefulRedisConnection<String, String>? = null
        var sync : RedisCommands<String, String>? = null
        val instanceConnection = HashMap<MiniGameInstance, StatefulRedisPubSubConnection<String, String>>()

        fun init(redisURI: RedisURI) {

            val redisServerUtil = RedisServerUtil()

            ServerUtil.default = redisServerUtil

            client = RedisClient.create(redisURI)!!
            connection = client!!.connect()
            sync = connection!!.sync()

            ProxyServer.getInstance().servers.forEach { (_, server) ->
                redisServerUtil.addServer(server)
            }
        }
    }

    override fun addServer(server : ServerInfo) {
        val connection = client!!.connectPubSub()
        connection.addListener(ServerPublishL(server))
        connection.sync().subscribe("${RedisKeyType.SERVER}:${server.name}:${RedisMessageType.BLOCK}")
        connection.sync().subscribe("${RedisKeyType.SERVER}:${server.name}:${RedisMessageType.ADDED_INSTANCE}")
        connection.sync().subscribe("${RedisKeyType.SERVER}:${server.name}:${RedisMessageType.DELETED_INSTANCE}")
    }

    override fun addLobby(serverInfo : ServerInfo) {
        val sync = connection?.sync()
        sync?.sadd(RedisKeyType.LOBBIES, serverInfo.name)
    }

    override fun removeLobby(serverInfo: ServerInfo) {
        val sync = connection?.sync()
        sync?.srem(RedisKeyType.LOBBIES, serverInfo.name)
    }

    override fun addMiniGame(name : String) {
        val sync = connection?.sync()
        sync?.sadd(RedisKeyType.MINI_GAMES, name)
    }

    override fun removeMiniGame(name : String) {
        val sync = connection?.sync()
        sync?.multi {
            del("${RedisKeyType.MINI_GAME}:$name:${RedisKeyType.SERVERS}")
            srem(RedisKeyType.MINI_GAMES, name)
        }
    }

    override fun addMiniGameServer(serverName: String, gameName: String) {
        val sync = connection?.sync()
        sync?.multi {
            sadd(RedisKeyType.MINI_GAMES, gameName)
            set("${RedisKeyType.SERVER}:$serverName:${RedisKeyType.MINI_GAMES}", gameName)
            sadd("${RedisKeyType.MINI_GAME}:${gameName}:${RedisKeyType.SERVERS}", serverName)
        }
    }

    override fun removeMiniGameServer(serverName: String, gameName: String) {
        val sync = connection?.sync()
        sync?.multi {
            del("${RedisKeyType.SERVER}:$serverName:${RedisKeyType.MINI_GAMES}")
            srem("${RedisKeyType.MINI_GAME}:${gameName}:${RedisKeyType.SERVERS}", serverName)
        }
    }
}