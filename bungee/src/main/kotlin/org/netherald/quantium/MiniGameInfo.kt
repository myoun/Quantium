package org.netherald.quantium

import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.netherald.quantium.data.instanceCount
import org.netherald.quantium.data.isBlocked
import org.netherald.quantium.data.maxInstanceCount
import org.netherald.quantium.exception.NotFoundServerException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

data class MiniGameInfo(
    val name : String,
    val minPlayerSize : Int,
    val maxPlayerSize : Int,
) {

    val queue : LinkedList<ProxiedPlayer> = LinkedList()
    val players : Collection<UUID> = HashSet()
    val instances : Collection<MiniGameInstance> = HashSet()
    val servers : Collection<ServerInfo> = HashSet()
    val maxInstanceCount : Map<ServerInfo, Int> = HashMap()
    val playerCount : Map<ServerInfo, Int> = HashMap()
    val startedInstanceCount : Map<ServerInfo, Int> = HashMap()

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

    val bestServer : ServerInfo
    get() {
        if (servers.none { !(
                it.isBlocked ||
                it.maxInstanceCount(this@MiniGameInfo) == it.instanceCount(this@MiniGameInfo)
                    ) }) throw NotFoundServerException()
        var server : ServerInfo? = null
        servers.forEach {
            server ?: run { server = it; return@forEach }
            if (server!!.players.size < it.players.size) { server = it }
        }
        return server!!
    }
}