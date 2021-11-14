package org.netherald.quantium

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.netherald.quantium.data.playingMiniGame
import org.netherald.quantium.exception.AlreadyPlayingException
import org.netherald.quantium.exception.AlreadyStartedException
import org.netherald.quantium.util.connectToLobby
import java.util.*
import kotlin.collections.HashSet

class MiniGameInstance(
    val uuid: UUID,
    val server : ServerInfo,
    val miniGame : MiniGameInfo
) {

    init {
        (miniGame.instances as MutableCollection<MiniGameInstance>) += this
    }

    fun delete() {
        players.forEach {
            ProxyServer.getInstance().getPlayer(it)?.connectToLobby()
        }
        (miniGame.instances as MutableCollection<MiniGameInstance>) -= this
    }

    var isStarted : Boolean = false
    val players : Collection<UUID> = HashSet()

    fun addPlayer(player: ProxiedPlayer) {
        player.playingMiniGame?.let { throw AlreadyPlayingException() }
        if (isStarted) { throw AlreadyStartedException() }
        (players as MutableCollection<UUID>).add(player.uniqueId)
        player.connect(server)
    }

    fun removePlayer(player: ProxiedPlayer) {
        if (player.playingMiniGame != this) {
            throw IllegalArgumentException("This player is not playing this instance")
        }
        (players as MutableCollection<UUID>).remove(player.uniqueId)
        player.connectToLobby()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MiniGameInstance

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}