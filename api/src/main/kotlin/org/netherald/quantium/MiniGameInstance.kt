package org.netherald.quantium

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.*
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.netherald.quantium.data.*
import org.netherald.quantium.setting.IsolatedSetting
import org.netherald.quantium.setting.TeamSetting
import org.netherald.quantium.setting.WorldSetting
import org.netherald.quantium.world.WorldEditor

@QuantiumMarker
class MiniGameInstance(
    val miniGame: MiniGame,
    val worlds : List<World> = ArrayList(),
    val reJoinData : MutableCollection<Player> = HashSet()
) {

    inner class UnSafe {
        fun callPlayerAdded(player: Player) {
            addedPlayerListener.forEach {
                it(BuilderUtil(this@MiniGameInstance), player)
            }
        }

        fun callPlayerRemoved(player: Player) {
            removedPlayerListener.forEach {
                it(BuilderUtil(this@MiniGameInstance), player)
            }
        }

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

        fun deleteAllWorld() {
            worlds.forEach { miniGame.worldInstanceMap.remove(it) }
            worlds.forEach { WorldEditor.default.deleteWorld(it) }
        }

        fun clearPlayersData() {
            players.forEach { player ->
                PlayerData.UnSafe.clearData(player)
            }
        }
    }

    var autoDelete : Boolean = false
    var autoStart : Boolean = true

    val isStarted : Boolean
        get() = started
    val isFinished : Boolean
        get() = finished

    private var started = false
    private var finished = false


    var teamSetting : TeamSetting = TeamSetting()
    var worldSetting: WorldSetting = WorldSetting()
    var isolatedSetting: IsolatedSetting = IsolatedSetting()


    val listeners = ArrayList<Listener>()
    val tasks = ArrayList<BukkitTask>()


    val players = HashSet<Player>()

    // it works when this minigame is team game
    val team : List<List<Player>> = ArrayList()

    val enableRejoin : Boolean = false
    var defaultGameMode : GameMode = GameMode.ADVENTURE

    var startTask : BukkitTask? = null
    /*
        it just for before start
     */
    fun addPlayer(player: Player) {
        PlayerData.UnSafe.addAllMiniGameData(player, this)
        UnSafe().callPlayerAdded(player)


        if (autoStart) {
            startTask ?: run {

                lateinit var taskData : BukkitTask
                var i = 15

                val sound = fun Player.() = playSound(location, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f)

                startTask = object : BukkitRunnable() {
                    override fun run() {
                        if (0 < i) {
                            if (i == 15 || i <= 5) {
                                broadcast("시작까지 ${i}초 전")
                                players.forEach { it.sound() }
                            }
                            i--
                        } else {
                            start()
                            taskData.cancel()
                        }
                    }
                }.runTaskTimer(miniGame.owner, 20, 0)
            }
        }
    }

    /*
        it just for before start
     */
    fun removePlayer(player: Player) {

        if (miniGame.maxPlayerSize < players.size+1) throw OutOfMaxPlayerSizeException()

        PlayerData.UnSafe.clearData(player)
        UnSafe().callPlayerRemoved(player)

        startTask?.let {
            if (players.size < miniGame.minPlayerSize) {
                it.cancel()
                startTask = null
            }
        }
    }








    fun broadcast(message : String) = players.forEach { it.sendMessage(message) }








    fun stopGame() {
        unregisterListeners()
        unregisterTasks()
        UnSafe().callStopListener()
        finished = true
        if (autoDelete) delete()
    }

    fun clearReJoinData() {
        reJoinData.forEach {
            it.clearReJoinData()
        }
    }

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

    fun delete() {
        if (enableRejoin) {
            players.forEach { player ->
                player.reJoinData = this@MiniGameInstance
            }
        }

        miniGame.instances as MutableList
        miniGame.instances.remove(this@MiniGameInstance)
        UnSafe().deleteAllWorld()
        UnSafe().clearPlayersData()

        if (miniGame.instances.size < miniGame.defaultInstanceSize) {
            miniGame.createInstance()
        }
    }

    fun start() {
        if (started || finished) return
        if (!worldSetting.enableOtherWorldTeleport) {
            listener(PlayerTeleportEvent::class.java) {
                if (
                    this@MiniGameInstance.worlds.contains(event.from.world) ||
                    this@MiniGameInstance.worlds.contains(event.to?.world)
                ) {
                    event.isCancelled = true
                }
            }
        }

        team as MutableList

        team.addAll(teamSetting.teamMatcher.match(players))

        listeners1.forEach {
            Bukkit.getServer().pluginManager.registerEvents(it, miniGame.owner)
            listeners.add(it)
        }

        players.forEach { player ->
            PlayerData.UnSafe.addAllMiniGameData(player, this@MiniGameInstance)
        }

        UnSafe().callStartListener()

    }





    private val addedPlayerListener = ArrayList<BuilderUtil.(player : Player) -> Unit>()
    fun onAddedPlayer(listener : BuilderUtil.() -> Unit) = stopListener.add(listener)

    private val removedPlayerListener = ArrayList<BuilderUtil.(player : Player) -> Unit>()
    fun onRemovedPlayer(listener : BuilderUtil.() -> Unit) = stopListener.add(listener)

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

    fun onPlayerRejoin(listener : QuantiumEvent<PlayerJoinEvent>.() -> Unit) : Listener? {
        return if (enableRejoin) {
            listener(PlayerJoinEvent::class.java, false, listener)
        } else {
            null
        }
    }











    private val listeners1 = ArrayList<Listener>()
    fun <T : Event> listener(
        clazz : Class<T>,
        allServerEvent : Boolean = false,
        listener: QuantiumEvent<T>.() -> Unit
    ) : Listener {
        val out : Listener
        if (started) {
            out = BuilderUtil(this).listener(clazz, allServerEvent, true, listener)
        } else {
            out = BuilderUtil(this).listener(clazz, allServerEvent, false, listener)
            listeners1.add(out)
        }
        return out
    }
}