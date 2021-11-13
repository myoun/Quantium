package org.netherald.quantium.util

import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import org.netherald.quantium.RedisKeyType

class RedisServerUtil(val serverName : String, redisURI: RedisURI) : ServerUtil {

    companion object {
        var instance : RedisServerUtil? = null
    }

    val client = RedisClient.create(redisURI)!!
    val connection: StatefulRedisConnection<String, String> = client.connect()

    private var blocked : Boolean = false
    override val isBlocked: Boolean get() = blocked

    override fun setBlockServer(value: Boolean) {
        connection.sync().publish("$serverName:block", value.toString())
        blocked = value
    }

    override val miniGames : Collection<String> get() = connection.sync().smembers(RedisKeyType.MINI_GAMES)
}