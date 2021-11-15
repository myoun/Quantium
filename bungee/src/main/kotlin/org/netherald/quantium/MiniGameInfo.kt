package org.netherald.quantium

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.netherald.quantium.data.PlayerData
import org.netherald.quantium.data.QuantiumConfig
import org.netherald.quantium.data.isBlocked
import org.netherald.quantium.event.PlayerJoinQueueEvent
import org.netherald.quantium.exception.NotFoundInstanceException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

data class MiniGameInfo(
    val name : String,
    val minPlayerSize : Int,
    val maxPlayerSize : Int,
) {

    val queue : Queue<ProxiedPlayer> = LinkedList()
    val players : Collection<UUID> = HashSet()
    val instances : Collection<MiniGameInstance> = HashSet()
    val servers : Collection<ServerInfo> = HashSet()
    val maxInstanceCount : Map<ServerInfo, Int> = HashMap()
    val playerCount : Map<ServerInfo, Int> = HashMap()
    val startedInstanceCount : Map<ServerInfo, Int> = HashMap() // TODO get data from redis

    fun addPlayer(player : ProxiedPlayer) {
        recommendMatchingInstance?.addPlayer(player) ?: run {
            if (!ProxyServer.getInstance().pluginManager
                    .callEvent(PlayerJoinQueueEvent(player, this)).isCancelled) {
                (queue as LinkedList<ProxiedPlayer>).add(player)
                PlayerData.playerQueueMiniGame[player] = this
                player.connect(QuantiumConfig.queueServer)
            }
        }
    }

    fun ServerInfo.countInstanceCount() {
        val map = (startedInstanceCount as MutableMap<ServerInfo, Int>)
        map[this] = map[this]!!+1
    }

    fun ServerInfo.discountInstanceCount() {
        val map = (startedInstanceCount as MutableMap<ServerInfo, Int>)
        map[this] = map[this]!!-1
    }
    /*
        player value can be null
     */
    fun poolPlayersInQueue(count : Int) : List<ProxiedPlayer> {
        val out = ArrayList<ProxiedPlayer>()
        val size = if (queue.size < count) queue.size else count
        for (i in 0 until size) {
            out.add(queue.poll())
        }
        return out
    }

    val recommendMatchingInstance : MiniGameInstance?
    get() {
        val instances = instances.filter { !(it.server.isBlocked || it.isStarted) }
        if (instances.isEmpty()) return null
        var out : MiniGameInstance? = null
        instances.forEach {
            if (out == null) run { out = it; return@forEach }
            if (out!!.players.size < it.players.size) out = it
        }
        return out
    }
}