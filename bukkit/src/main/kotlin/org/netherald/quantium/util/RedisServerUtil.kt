package org.netherald.quantium.util

import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import org.netherald.quantium.MiniGameInstance
import org.netherald.quantium.RedisKeyType
import java.util.*

class RedisServerUtil(val serverName : String, redisURI: RedisURI) : ServerUtil {

    companion object {

        var instance : RedisServerUtil? = null

        var client : RedisClient? = null
        var connection: StatefulRedisConnection<String, String>? = null
        var sync : RedisCommands<String, String>? = null

    }

    init {
        client = RedisClient.create(redisURI)!!
        connection = client?.connect()
        sync = connection?.sync()
    }

    private var blocked : Boolean = false
    override val isBlocked: Boolean get() = blocked

    override fun setBlockServer(value: Boolean) {
        sync?.publish("$serverName:block", value.toString())
        blocked = value
    }

    override val miniGames : Collection<String> get() = sync?.smembers(RedisKeyType.MINI_GAMES)!!

    override fun getInstances(game : String) : Collection<UUID> =
        sync?.smembers("$game:${RedisKeyType.INSTANCES}")!!.map {
            UUID.fromString(it)
        }

    fun addMiniGame(name : String) {
        sync?.let {
            it.sadd(RedisKeyType.MINI_GAMES, name)
            it.sadd("${RedisKeyType.MINI_GAME}:$name:${RedisKeyType.SERVERS}", serverName)
            it.sadd("${RedisKeyType.SERVER}:$serverName:${RedisKeyType.MINI_GAMES}", name)
        }
    }

    fun removeMiniGame(name : String) {
        sync?.let {
            it.srem(RedisKeyType.MINI_GAMES, name)
            it.srem("${RedisKeyType.MINI_GAME}:$name:${RedisKeyType.SERVERS}", serverName)
            it.srem("${RedisKeyType.SERVER}:$serverName:${RedisKeyType.MINI_GAMES}", name)
        }
    }

    fun addInstance(instance : MiniGameInstance) {
        sync?.let {
            it.sadd(
                "${RedisKeyType.MINI_GAME}:${instance.miniGame.name}:${RedisKeyType.INSTANCES}",
                instance.uuid.toString()
            )
            it.sadd("${RedisKeyType.INSTANCE}:${instance.uuid}:${RedisKeyType.SERVER}",
                serverName
            )
            it.sadd("${RedisKeyType.SERVER}:$serverName:${RedisKeyType.INSTANCES}",
                instance.uuid.toString()
            )
        }
    }
}