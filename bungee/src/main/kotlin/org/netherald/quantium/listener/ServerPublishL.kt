package org.netherald.quantium.listener

import io.lettuce.core.pubsub.RedisPubSubAdapter
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.plugin.Event
import org.netherald.quantium.MiniGameInstance
import org.netherald.quantium.RedisKeyType
import org.netherald.quantium.RedisMessageType
import org.netherald.quantium.data.MiniGameData
import org.netherald.quantium.data.addMiniGameServer
import org.netherald.quantium.data.isBlocked
import org.netherald.quantium.data.removeMiniGameServer
import org.netherald.quantium.debug
import org.netherald.quantium.event.*
import org.netherald.quantium.util.RedisServerUtil
import java.util.*

class ServerPublishL(val server : ServerInfo) : RedisPubSubAdapter<String, String>() {
    override fun message(channel: String, message: String) {
        if (channel.split(":")[0] != RedisKeyType.SERVER) return

        if (server.name != channel.split(":")[1]) return

        val callEvent = fun (event : Event) = ProxyServer.getInstance().pluginManager.callEvent(event)

        when (channel.split(":")[2]) {
            RedisMessageType.BLOCK -> {
                server.isBlocked = message.toBoolean()
            }

            RedisMessageType.ADDED_INSTANCE -> {
                val uuid = UUID.fromString(message)
                val minigameName = RedisServerUtil.sync!!.get(
                    "${RedisKeyType.INSTANCE}:$uuid:${RedisKeyType.MINI_GAME}"
                )
                val miniGame = MiniGameData.miniGames[minigameName]!!
                val instance = MiniGameInstance(uuid, server, miniGame)

                val connection = RedisServerUtil.client!!.connectPubSub()
                connection.addListener(InstancePublishL(instance))

                connection.sync().subscribe("${RedisKeyType.INSTANCE}:$uuid:${RedisKeyType.INSTANCE_STARTED}")
                connection.sync().subscribe("${RedisKeyType.INSTANCE}:$uuid:${RedisKeyType.INSTANCE_STOPPED}")
                RedisServerUtil.instanceConnection[instance] = connection
                miniGame.addInstance(instance)
                callEvent(InstanceAddedEvent(instance))
            }

            RedisMessageType.DELETED_INSTANCE -> {
                val uuid = UUID.fromString(message)
                val instance = MiniGameData.instances[uuid]!!
                instance.delete()
                RedisServerUtil.instanceConnection[instance]!!.closeAsync()
                callEvent(InstanceDeletedEvent(instance))
            }

            RedisMessageType.MINI_GAME_ADDED -> {
                val miniGameName = message.split("|")[0]
                val miniGame = MiniGameData.miniGames[miniGameName]!!
                server.addMiniGameServer(miniGame, message.split("|")[1].toInt())
                callEvent(ServerMiniGameAddedEvent(server, miniGame))
            }

            RedisMessageType.MINI_GAME_REMOVED -> {
                val miniGame = MiniGameData.miniGames[message]!!
                server.removeMiniGameServer(MiniGameData.miniGames[message]!!)
                callEvent(ServerMiniGameRemovedEvent(server, miniGame))
            }
        }
    }
}