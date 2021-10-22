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
import org.netherald.quantium.exception.OutOfMaxPlayerSizeException
import org.netherald.quantium.setting.IsolatedSetting
import org.netherald.quantium.setting.TeamSetting
import org.netherald.quantium.setting.WorldSetting
import org.netherald.quantium.util.SpectatorUtil
import org.netherald.quantium.world.WorldEditor

@QuantiumMarker
class MiniGameInstance(
    val miniGame: MiniGame,
    val reJoinData : MutableCollection<Player> = HashSet()
) {

    inner class UnSafe {

        var world : World? = null
        var worldNether : World? = null
        var worldEnder : World? = null
        var otherWorlds = HashSet<World>()

        fun start() {

            finished = false

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
            listeners1.clear()

            players.forEach { player ->
                PlayerData.UnSafe.addAllMiniGameData(player, this@MiniGameInstance)
            }

            callStartListener()

            started = true

        }

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

        fun callDeleteListener() {
            deleteListener.forEach {
                it(BuilderUtil(this@MiniGameInstance))
            }
        }

        fun deleteAllWorld() {
            worlds.forEach { miniGame.worldInstanceMap.remove(it) }
            worlds.forEach { worldSetting.worldEditor.deleteWorld(it) }
        }

        fun clearPlayersData() {
            players.forEach { player ->
                PlayerData.UnSafe.clearData(player)
            }
        }
    }

    val world : World?
        get() = UnSafe().world
    val worldNether : World?
        get() = UnSafe().worldNether
    val worldEnder : World?
        get() = UnSafe().worldEnder
    val otherWorlds : Collection<World>
        get() = UnSafe().otherWorlds

    val worlds : Collection<World>
        get() {
            val out = HashSet<World>(UnSafe().otherWorlds)
            UnSafe().world?.let { out.add(it) }
            UnSafe().worldNether?.let { out.add(it) }
            UnSafe().worldEnder?.let { out.add(it) }
            UnSafe().otherWorlds.forEach { out.add(it) }
            return out
        }

    var autoDelete : Boolean = true
    var autoStart : Boolean = true

    val isStarted : Boolean
        get() = started
    val isFinished : Boolean
        get() = finished

    private var started = false
    private var finished = false

    var spectatorUtil: SpectatorUtil = SpectatorUtil.default

    var teamSetting : TeamSetting = TeamSetting()
    var worldSetting: WorldSetting = WorldSetting()
    var isolatedSetting: IsolatedSetting = IsolatedSetting()

    fun teamSetting(init : TeamSetting.() -> Unit) {
        teamSetting.init()
    }

    fun worldSetting(init : WorldSetting.() -> Unit) {
        worldSetting.init()
    }

    fun isolatedSetting(init : IsolatedSetting.() -> Unit) {
        isolatedSetting.init()
    }

    val listeners = ArrayList<Listener>()
    val tasks = ArrayList<BukkitTask>()


    val players = HashSet<Player>()

    // it works when this minigame is team game
    val team : List<List<Player>> = ArrayList()

    var enableRejoin : Boolean = false
    var defaultGameMode : GameMode = GameMode.ADVENTURE

    private var startTask : BukkitTask? = null

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
                            UnSafe().start()
                            taskData.cancel()
                        }
                    }
                }.runTaskTimer(miniGame.owner, 20, 20)
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

    fun addWorld(world : World) {
        UnSafe().otherWorlds.add(world)
    }






    fun broadcast(message : String) = players.forEach { it.sendMessage(message) }








    fun stopGame() {
        unregisterListeners()
        unregisterTasks()
        players.forEach { spectatorUtil.unApplySpectator(it) }
        UnSafe().callStopListener()
        if (autoDelete) delete()
        finished = true
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

        UnSafe().callDeleteListener()

        if (miniGame.instances.size < miniGame.defaultInstanceSize) {
            miniGame.createInstance()
        }
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

    private val deleteListener = ArrayList<BuilderUtil.() -> Unit>()
    fun onDelete(listener : BuilderUtil.() -> Unit) = stopListener.add(listener)






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
        if (started || finished) {
            out = BuilderUtil(this).listener(clazz, allServerEvent, true, listener)
        } else {
            out = BuilderUtil(this).listener(clazz, allServerEvent, false, listener)
            listeners1.add(out)
        }
        return out
    }
}