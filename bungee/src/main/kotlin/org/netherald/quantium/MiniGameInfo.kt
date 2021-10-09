package org.netherald.quantium

import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

data class MiniGameInfo(
    val name : String,
    val queue : LinkedList<ProxiedPlayer> = LinkedList(),
    val players : ArrayList<UUID> = ArrayList(),
    val maxInstanceCount : Map<ServerInfo, Int> = HashMap()
) {
    /*
        player value can be null
     */
    fun poolPlayersInQueue(count : Int) : List<ProxiedPlayer> {
        val out = ArrayList<ProxiedPlayer>()
        for (i in 0 until count) {
            out.add(queue.poll())
        }
        return out
    }
}