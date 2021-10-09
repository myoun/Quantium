package org.netherald.quantium

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.*
import org.bukkit.scheduler.BukkitTask
import org.netherald.quantium.dataclass.*

@QuantiumMarker
class MiniGameInstance(
    val miniGame: MiniGame,
    val worlds : List<String>
) {

    inner class unsafe() {
        fun callMiniGameCreatedListener() {
            miniGameCreatedListener.forEach {
                it(BuilderUtil(this@MiniGameInstance))
            }
        }

        fun callInstanceCreatedListener() {
            instanceCreatedListener.forEach {
                it(BuilderUtil(this@MiniGameInstance))
            }
        }

        fun callStartListener() {
            startListener.forEach {
                it(BuilderUtil(this@MiniGameInstance))
            }
        }

        fun callStopListener() {
            stopListener.forEach {
                it(BuilderUtil(this@MiniGameInstance))
            }
        }
    }

    val listeners = ArrayList<Listener>()
    val tasks = ArrayList<BukkitTask>()

    val players : MutableList<Player> = ArrayList()
    var matcher : TeamMatcher = DefaultTeamMatcher(miniGame.teamSetting)

    init {
        if (!miniGame.worldSetting.enableOtherWorldTeleport) {
            listener(PlayerTeleportEvent::class.java) {
                if (worlds.contains(event.from.world.name)) event.isCancelled = true
            }
        }

        unsafe().callInstanceCreatedListener()
    }

    fun teamMatcher(code: (List<Player>) -> List<List<Player>>) {
        matcher = object : TeamMatcher() {
            override fun match(players: List<Player>): List<List<Player>> {
                return code(players)
            }
        }
    }

    fun broadcast(message : String) = players.forEach { it.sendMessage(message) }


    fun stopGame() {
        unregisterListeners()
        unregisterTasks()
        unsafe().callStopListener()
        clearReJoinData()
    }

    private val miniGameCreatedListener = ArrayList<BuilderUtil.() -> Unit>()
    fun onMiniGameCreated(listener : BuilderUtil.() -> Unit) = miniGameCreatedListener.add(listener)

    private val instanceCreatedListener = ArrayList<BuilderUtil.() -> Unit>()
    fun onInstanceCreated(listener : BuilderUtil.() -> Unit) = instanceCreatedListener.add(listener)

    private val startListener = ArrayList<BuilderUtil.() -> Unit>()
    fun onStart(listener : BuilderUtil.() -> Unit) = startListener.add(listener)

    private val stopListener = ArrayList<BuilderUtil.() -> Unit>()
    fun onStop(listener : BuilderUtil.() -> Unit) = stopListener.add(listener)

    // why don't check if player is playing this minigame?
    // because allServerEvent option is false

    fun onPlayerKicked(listener : QuantiumEvent<PlayerKickEvent>.() -> Unit) : Listener =
        listener(PlayerKickEvent::class.java, false, listener)

    fun onPlayerDisconnected(listener : QuantiumEvent<PlayerQuitEvent>.() -> Unit) : Listener =
        listener(PlayerQuitEvent::class.java, false, listener)

    fun onPlayerRejoin(listener : QuantiumEvent<PlayerJoinEvent>.() -> Unit) : Listener =
        listener(PlayerJoinEvent::class.java, false, listener)


    fun unregisterListeners() {
        listeners.forEach {
            HandlerList.unregisterAll(it)
        }
        listeners.clear()
    }

    fun unregisterTasks() {
        tasks.forEach {
            it.cancel()
        }
        tasks.clear()
    }

    private fun <T : Event> listener(
        clazz : Class<T>,
        allServerEvent : Boolean = false,
        listener: QuantiumEvent<T>.() -> Unit
    ) = BuilderUtil(this).listener(clazz, allServerEvent, listener)
}