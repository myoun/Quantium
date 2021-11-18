package org.netherald.quantium.listener

import com.google.common.io.ByteStreams
import io.lettuce.core.api.sync.multi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import event.*
import org.netherald.quantium.*
import org.netherald.quantium.util.PluginMessageServerUtil
import org.netherald.quantium.util.RedisServerUtil

class MiniGameDataL : Listener {

    private val serverName = RedisServerUtil.instance!!.serverName

    private fun publish(instance : MiniGameInstance, channel : String, value : String) {
        RedisServerUtil.sync?.apply {
            publish(
                "${RedisKeyType.INSTANCE}:${instance.uuid}:${channel}",
                value
            )
        }
    }

    private fun miniGamePublish(miniGame : MiniGame, channel : String, value : String) {
        RedisServerUtil.sync?.apply {
            publish(
                "${RedisKeyType.MINI_GAME}:${miniGame.name}:${channel}",
                value
            )
        }
    }

    private fun serverPublish(channel : String, value : String) {
        RedisServerUtil.sync?.apply {
            publish("${RedisKeyType.SERVER}:${serverName}:${channel}", value)
        }
    }

    @EventHandler
    fun onMiniGameCreated(event : MiniGameCreateEvent) {
        RedisServerUtil.sync?.multi {
            serverPublish(
                RedisMessageType.MINI_GAME_ADDED,
                "${event.miniGame.name}|${event.miniGame.maxInstanceSize}"
            )
            sadd("${RedisKeyType.SERVER}:${serverName}:${RedisKeyType.MINI_GAMES}", event.miniGame.name)
            sadd("${RedisKeyType.MINI_GAME}:${event.miniGame.name}:${RedisKeyType.SERVERS}", serverName)
        }
    }

    @EventHandler
    fun onMiniGameDeleted(event : MiniGameDeletedEvent) {
        RedisServerUtil.sync?.multi {
            serverPublish(RedisMessageType.MINI_GAME_REMOVED, event.miniGame.name)
            srem("${RedisKeyType.SERVER}:${serverName}:${RedisKeyType.MINI_GAMES}", event.miniGame.name)
            srem("${RedisKeyType.MINI_GAME}:${event.miniGame.name}:${RedisKeyType.SERVERS}", serverName)
        }
    }

    @EventHandler
    fun onCreated(event : InstanceCreatedEvent) {
        RedisServerUtil.sync?.multi {
            set("${RedisKeyType.INSTANCE}:${event.instance.uuid}:${RedisKeyType.SERVER}", serverName)
            sadd(
                "${RedisKeyType.SERVER}:${serverName}:${RedisKeyType.INSTANCES}",
                event.instance.uuid.toString()
            )
            sadd(
                "${RedisKeyType.MINI_GAME}:${event.instance.miniGame.name}:${RedisKeyType.INSTANCES}",
                event.instance.uuid.toString()
            )
            set(
                "${RedisKeyType.INSTANCE}:${event.instance.uuid}:${RedisKeyType.MINI_GAME}",
                event.instance.miniGame.name
            )
            set(
                "${RedisKeyType.INSTANCE}:${event.instance.uuid}:${RedisKeyType.SERVER}",
                serverName
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
            serverPublish(RedisMessageType.DELETED_INSTANCE, event.instance.uuid.toString())
            del("${RedisKeyType.INSTANCE}:${event.instance.uuid}:${RedisKeyType.SERVER}")
            srem(
                "${RedisKeyType.SERVER}:${serverName}:${RedisKeyType.INSTANCES}",
                event.instance.uuid.toString()
            )
            srem(
                "${RedisKeyType.MINI_GAME}:${event.instance.miniGame.name}:${RedisKeyType.INSTANCES}",
                event.instance.uuid.toString()
            )
            del("${RedisKeyType.INSTANCE}:${event.instance.uuid}:${RedisKeyType.MINI_GAME}")
            del("${RedisKeyType.INSTANCE}:${event.instance.uuid}:${RedisKeyType.SERVER}")
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
            @Suppress("UnstableApiUsage")
            val out = ByteStreams.newDataOutput()
            out.writeUTF(Channels.SubChannels.Bukkit.STARTED_INSTANCE)
            out.writeUTF(event.instance.uuid.toString())
            PluginMessageServerUtil.instance!!.sendPluginMessage(out.toByteArray())
        }
    }

    @EventHandler
    fun onStopped(event : InstanceStoppedEvent) {
        RedisServerUtil.sync?.multi {
            sadd(RedisKeyType.INSTANCE_STOPPED, event.instance.uuid.toString())
            publish(event.instance, RedisMessageType.STOPPED_INSTANCE, event.instance.uuid.toString())
        } ?: run {
            @Suppress("UnstableApiUsage")
            val out = ByteStreams.newDataOutput()
            out.writeUTF(Channels.SubChannels.Bukkit.STOPPED_INSTANCE)
            out.writeUTF(event.instance.uuid.toString())
            PluginMessageServerUtil.instance!!.sendPluginMessage(out.toByteArray())
        }
    }

    @EventHandler
    fun onReJoinDataAdd(event : InstanceAddReJoinDataEvent) {
        RedisServerUtil.sync?.multi {
            sadd(
                "${RedisKeyType.INSTANCE}:${event.instance.uuid}:${RedisKeyType.REJOIN_DATA}",
                event.player.uniqueId.toString()
            )
            publish(event.instance, RedisMessageType.REJOIN_DATA_ADD, event.player.uniqueId.toString())
        } ?: run {
            @Suppress("UnstableApiUsage")
            val out = ByteStreams.newDataOutput()
            out.writeUTF(Channels.SubChannels.Bukkit.ADDED_REJOIN_DATA)
            out.writeUTF(event.instance.uuid.toString())
            out.writeUTF(event.player.name)
            PluginMessageServerUtil.instance!!.sendPluginMessage(out.toByteArray())
        }
    }

    @EventHandler
    fun onReJoinDataRemove(event : InstanceRemoveReJoinDataEvent) {
        RedisServerUtil.sync?.multi {
            srem(
                "${RedisKeyType.INSTANCE}:${event.instance.uuid}:${RedisKeyType.REJOIN_DATA}",
                event.player.uniqueId.toString()
            )
            publish(event.instance, RedisMessageType.REJOIN_DATA_REMOVE, event.player.uniqueId.toString())
        } ?: run {
            @Suppress("UnstableApiUsage")
            val out = ByteStreams.newDataOutput()
            out.writeUTF(Channels.SubChannels.Bukkit.REMOVED_REJOIN_DATA)
            out.writeUTF(event.instance.uuid.toString())
            out.writeUTF(event.player.name)
            PluginMessageServerUtil.instance!!.sendPluginMessage(out.toByteArray())
        }
    }
}
