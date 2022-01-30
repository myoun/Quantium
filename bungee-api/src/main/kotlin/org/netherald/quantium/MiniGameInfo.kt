package org.netherald.quantium

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.netherald.quantium.data.MiniGameData
import org.netherald.quantium.data.PlayerData
import org.netherald.quantium.data.QuantiumConfig
import org.netherald.quantium.data.isBlocked
import org.netherald.quantium.event.PlayerJoinQueueEvent
import org.netherald.quantium.util.bestServer
import java.lang.IllegalStateException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

data class MiniGameInfo(
    val name : String,
    val minPlayerSize : Int,
    val maxPlayerSize : Int
) {

    val queue : Queue<ProxiedPlayer> = LinkedList()
    val players : Collection<UUID> = HashSet()
    val instances : Collection<MiniGameInstance> = HashSet()
    val servers : Collection<ServerInfo> = HashSet()
    val maxInstanceCount : Map<ServerInfo, Int> = HashMap()
    val playerCount : Map<ServerInfo, Int> = HashMap()
    val startedInstanceCount : Map<ServerInfo, Int> = HashMap() // TODO get data from redis

    fun addInstance(miniGameInstance: MiniGameInstance) {
        if (miniGameInstance.miniGame != this) throw IllegalArgumentException("Wrong miniGameInstance")
        (instances as MutableCollection<MiniGameInstance>).add(miniGameInstance)
        MiniGameData.instances[miniGameInstance.uuid] = miniGameInstance
        debug("$name's instance ${miniGameInstance.uuid} is added")
    }

    fun addPlayer(player : ProxiedPlayer) {
        debug("$name add player ${player.name}")
        recommendMatchingInstance?.addPlayer(player) ?: run { queuePlayer(player) }
    }

    fun queuePlayer(player: ProxiedPlayer) {
        if (queue.contains(player)) throw IllegalStateException("Already in queue!")
        debug("Queue player ${player.name}")
        if (!ProxyServer.getInstance().pluginManager
                .callEvent(PlayerJoinQueueEvent(player, this)).isCancelled) {
            debug("Not cancelled")
            queue.add(player)
            PlayerData.playerQueueMiniGame[player] = this
            player.connect(QuantiumConfig.queueServers.bestServer)
        } else debug("Cancelled")
    }

    fun ServerInfo.countInstanceCount() {
        val map = (startedInstanceCount as MutableMap<ServerInfo, Int>)
        map[this] ?: run { map[this] = 0 }
        map[this] = map[this]!!+1
    }

    fun ServerInfo.discountInstanceCount() {
        val map = (startedInstanceCount as MutableMap<ServerInfo, Int>)
        map[this] ?: run { map[this] = 0 }
        map[this] = map[this]!!-1
    }

    fun poolPlayer() : ProxiedPlayer? {
        debug("$name pool player")
        val player = queue.poll()
        player?.let {
            debug(it.name)
            PlayerData.playerQueueMiniGame -= player
            return player
        }
        debug("null")
        return null
    }

    /*
        player value can be null
     */
    fun poolPlayersInQueue(count : Int) : List<ProxiedPlayer> {
        val size = if (queue.size < count) queue.size else count
        debug("$name pool $size. Input: $count")
        val out = ArrayList<ProxiedPlayer>(size)
        for (i in 0 until size) { out.add(poolPlayer()!!) }
        return out
    }

    val recommendMatchingInstance : MiniGameInstance?
    get() {
        val instances = instances.filter { !(it.server.isBlocked || it.isStarted || it.players.size == maxPlayerSize) }
        if (instances.isEmpty()) return null
        var out : MiniGameInstance? = null
        instances.forEach {
            if (out == null) run { out = it; return@forEach }
            if (out!!.players.size < it.players.size) out = it
        }
        return out
    }
}