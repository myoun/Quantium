package org.netherald.quantium

import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.*
import org.bukkit.scheduler.BukkitTask
import org.netherald.quantium.data.*
import org.netherald.quantium.exception.OutOfMaxPlayerSizeException
import org.netherald.quantium.setting.IsolationSetting
import org.netherald.quantium.setting.TeamSetting
import org.netherald.quantium.setting.WorldSetting
import org.netherald.quantium.util.MiniGameBuilderUtil
import org.netherald.quantium.util.SpectatorUtil
import org.netherald.quantium.world.PortalLinker

@QuantiumMarker
class MiniGameInstance(
    val miniGame: MiniGame,
) {

    inner class UnSafe {

        var world : World? = null
        var worldNether : World? = null
        var worldEnder : World? = null
        val otherWorlds = HashSet<World>()

        fun start() {

            finished = false

            if (!worldSetting.enableOtherWorldTeleport) {
                listener(PlayerTeleportEvent::class.java) {
                    if (
                        this@MiniGameInstance.worlds.contains(event.from.world) xor
                        this@MiniGameInstance.worlds.contains(event.to?.world)
                    ) {
                        event.isCancelled = true
                    }
                }
            }

            if (teamSetting.enable) {
                team = teamSetting.teamMatcher.match(players)
            }

            if (worldSetting.linkPortal) {
                val linker : PortalLinker = worldSetting.portalLinker
                world?.let { world ->
                    worldNether?.let { linker.linkNether(world, it) }
                    worldEnder?.let { linker.linkEnder(world, it) }
                }
            }

            if (isolatationSetting.perChat) {
                isolatationSetting.perMiniGameChatUtil.applyPerChat(this@MiniGameInstance)
            }

            if (isolatationSetting.perPlayerList) {
                isolatationSetting.perMiniGameTabListUtil.applyPerTabList(this@MiniGameInstance)
            }

            registerListeners()

            players.forEach { player ->
                PlayerData.UnSafe.addAllMiniGameData(player, this@MiniGameInstance)
                worldSetting.spawn?.let { player.teleport(it) }
            }

            started = true

            callStartListener()
            println("""
                ${miniGame.name} instance started
                    world : ${world?.name}
                    nether : ${worldNether?.name}
                    ender : ${worldEnder?.name}
            """.trimIndent())

        }

        fun callPlayerAdded(player: Player) {
            addedPlayerListener.forEach {
                it(MiniGameBuilderUtil(this@MiniGameInstance), player)
            }
        }

        fun callPlayerRemoved(player: Player) {
            removedPlayerListener.forEach {
                it(MiniGameBuilderUtil(this@MiniGameInstance), player)
            }
        }

        fun callMiniGameCreatedListener() {
            miniGameCreatedListener.forEach {
                it(MiniGameBuilderUtil(this@MiniGameInstance))
            }
        }

        fun callInstanceCreatedListener() {
            instanceCreatedListener.forEach {
                it(MiniGameBuilderUtil(this@MiniGameInstance))
            }
        }

        fun callStartListener() {
            startListener.forEach {
                it(MiniGameBuilderUtil(this@MiniGameInstance))
            }
        }

        fun callStopListener() {
            stopListener.forEach {
                it(MiniGameBuilderUtil(this@MiniGameInstance))
            }
        }

        fun callDeleteListener() {
            deleteListener.forEach {
                it(MiniGameBuilderUtil(this@MiniGameInstance))
            }
        }

        fun deleteAllWorld() {
            worlds.forEach { worldSetting.worldEditor.deleteWorld(it) }
            worlds.forEach { miniGame.worldInstanceMap -= it }
            UnSafe().world = null
            UnSafe().worldNether = null
            UnSafe().worldEnder = null
            UnSafe().otherWorlds.clear()
        }

        fun clearPlayersData() {
            players.forEach { player ->
                PlayerData.UnSafe.clearData(player)
            }
        }
    }

    val world : World? get() = UnSafe().world
    val worldNether : World? get() = UnSafe().worldNether
    val worldEnder : World? get() = UnSafe().worldEnder
    val otherWorlds : Collection<World> get() = UnSafe().otherWorlds

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

    val isStarted : Boolean get() = started
    val isFinished : Boolean get() = finished

    private var started = false
    private var finished = false

    var spectatorUtil: SpectatorUtil = SpectatorUtil.default

    var teamSetting : TeamSetting = TeamSetting()
    var worldSetting: WorldSetting = WorldSetting()
    var isolatationSetting: IsolationSetting = IsolationSetting()

    val reJoinData : MutableCollection<Player> = HashSet()

    fun teamSetting(init : TeamSetting.() -> Unit) {
        teamSetting.init()
    }

    fun worldSetting(init : WorldSetting.() -> Unit) {
        worldSetting.init()
    }

    fun isolatedSetting(init : IsolationSetting.() -> Unit) {
        isolatationSetting.init()
    }

    val listeners = ArrayList<Listener>()
    val tasks = ArrayList<QuantiumTaskData>()


    val players = HashSet<Player>()

    // it works when this minigame is team game
    var team : List<List<Player>> = ArrayList()

    var enableRejoin : Boolean = false
    var defaultGameMode : GameMode = GameMode.ADVENTURE

    private var startTask : BukkitTask? = null

    fun runStartTask() {
        startTask ?: run {

            val sound = fun Player.() = playSound(location, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f)
            val soundBroadCast = fun () = players.forEach { it.sound() }

            startTask = MiniGameBuilderUtil(this).loopTask(15 downTo 0, 20, 20) { i ->
                if (0 < i) {
                    if (i == 15 || i <= 5) {
                        broadcast("시작까지 ${i}초 전")
                        soundBroadCast()
                    }
                } else {
                    cancelStartTask()
                    UnSafe().start()
                }
            }
        }
    }

    fun cancelStartTask() {
        startTask?.cancel()
        startTask = null
    }

    fun addPlayer(player: Player) {

        if (miniGame.maxPlayerSize < players.size+1) throw OutOfMaxPlayerSizeException()

        PlayerData.UnSafe.addAllMiniGameData(player, this)
        UnSafe().callPlayerAdded(player)
        if (autoStart && miniGame.minPlayerSize <= players.size) {
            runStartTask()
        }
    }

    /*
        it just for before start
     */
    fun removePlayer(player: Player) {

        PlayerData.UnSafe.clearData(player)
        UnSafe().callPlayerRemoved(player)

        if (players.size < miniGame.minPlayerSize) {
            cancelStartTask()
        }
    }

    fun addWorld(value : World, addWorldType: AddWorldType) {
        val removeData = fun (world : World?) = world?.let { miniGame.worldInstanceMap -= world }
        when (addWorldType) {
            AddWorldType.NORMAL -> {
                removeData(world)
                UnSafe().world = value
            }
            AddWorldType.NETHER -> {
                removeData(worldNether)
                UnSafe().worldNether = value
            }
            AddWorldType.ENDER -> {
                removeData(worldEnder)
                UnSafe().worldEnder = value
            }
            AddWorldType.OTHER -> {
                UnSafe().otherWorlds += value
            }
        }
        miniGame.worldInstanceMap[value] = this
    }

    fun removeWorld(value : World) {
        if (UnSafe().world == value) UnSafe().world = null
        if (UnSafe().worldNether == value) UnSafe().worldNether = null
        if (UnSafe().worldEnder == value) UnSafe().worldEnder = null
        UnSafe().otherWorlds -= value
        miniGame.worldInstanceMap -= value
    }

    enum class AddWorldType {
        NORMAL, NETHER, ENDER, OTHER
    }






    fun broadcast(message : String) = players.forEach { it.sendMessage(message) }








    fun stopGame() {
        unregisterListeners()
        unregisterTasks()
        players.forEach { spectatorUtil.unApplySpectator(it) }
        UnSafe().callStopListener()
        if (autoDelete) delete()
        finished = true
        println("${miniGame.name} instance is stopped")
    }

    fun clearReJoinData() {
        reJoinData.forEach { it.clearReJoinData() }
    }

    fun unregisterListeners() {
        listeners.forEach { HandlerList.unregisterAll(it) }
        listeners.clear()
    }

    fun unregisterTasks() {
        tasks.forEach { it.cancel() }
        tasks.clear()
    }

    fun delete() {

        miniGame.instances as MutableList
        miniGame.instances.remove(this@MiniGameInstance)

        UnSafe().deleteAllWorld()
        UnSafe().clearPlayersData()
        UnSafe().callDeleteListener()

        if (miniGame.instances.size < miniGame.defaultInstanceSize) {
            miniGame.createInstance()
        }

    }





    private val addedPlayerListener = ArrayList<MiniGameBuilderUtil.(player : Player) -> Unit>()
    fun onPlayerAdded(listener : MiniGameBuilderUtil.(player : Player) -> Unit) = addedPlayerListener.add(listener)

    private val removedPlayerListener = ArrayList<MiniGameBuilderUtil.(player : Player) -> Unit>()
    fun onPlayerRemoved(listener : MiniGameBuilderUtil.(player : Player) -> Unit) = removedPlayerListener.add(listener)

    private val miniGameCreatedListener = ArrayList<MiniGameBuilderUtil.() -> Unit>()
    fun onMiniGameCreated(listener : MiniGameBuilderUtil.() -> Unit) = miniGameCreatedListener.add(listener)

    private val instanceCreatedListener = ArrayList<MiniGameBuilderUtil.() -> Unit>()
    fun onInstanceCreated(listener : MiniGameBuilderUtil.() -> Unit) = instanceCreatedListener.add(listener)

    private val startListener = ArrayList<MiniGameBuilderUtil.() -> Unit>()
    fun onStart(listener : MiniGameBuilderUtil.() -> Unit) = startListener.add(listener)

    private val stopListener = ArrayList<MiniGameBuilderUtil.() -> Unit>()
    fun onStop(listener : MiniGameBuilderUtil.() -> Unit) = stopListener.add(listener)

    private val deleteListener = ArrayList<MiniGameBuilderUtil.() -> Unit>()
    fun onDelete(listener : MiniGameBuilderUtil.() -> Unit) = stopListener.add(listener)






    // why don't check if player is playing this minigame?
    // because allServerEvent option is false

    fun onPlayerKicked(listener : QuantiumEvent<out PlayerKickEvent>.() -> Unit) : Listener =
        listener(PlayerKickEvent::class.java, EventPriority.NORMAL,
            ignoreCancelled = true,
            allServerEvent = false,
            listener = listener
        )

    fun onPlayerDisconnected(listener : QuantiumEvent<out PlayerQuitEvent>.() -> Unit) : Listener =
        listener(
            PlayerQuitEvent::class.java, EventPriority.NORMAL,
            ignoreCancelled = true,
            allServerEvent = false,
            listener = listener
        )

    fun onPlayerRejoin(listener : QuantiumEvent<out PlayerJoinEvent>.() -> Unit) : Listener? {
        return if (enableRejoin) {
            listener(PlayerJoinEvent::class.java, EventPriority.NORMAL,
                ignoreCancelled = true,
                allServerEvent = false,
                listener = listener
            )
        } else {
            null
        }
    }











    private val listeners1 = HashMap<Listener, Class<out Event>>()
    private val eventPriorityData = HashMap<Listener, EventPriority>()
    private val ignoreCancelledData = HashMap<Listener, Boolean>()

    fun <T : Event> listener(
        clazz: Class<out T>,
        eventPriority : EventPriority = EventPriority.NORMAL,
        ignoreCancelled : Boolean = false,
        allServerEvent: Boolean = false,
        listener: QuantiumEvent<out T>.() -> Unit
    ) : Listener {
        val out = MiniGameBuilderUtil(this).listener(
            clazz, allServerEvent, eventPriority, ignoreCancelled, false, listener
        )
        listeners1[out] = clazz
        ignoreCancelledData[out] = ignoreCancelled
        eventPriorityData[out] = eventPriority
        return out
    }

    private val listeners2 = ArrayList<Listener>()
    fun registerEvents(listener: Listener) {
        listeners2.add(listener)
    }

    private fun registerListeners() {

        listeners1.forEach { (listener, _) -> registerListener(listener) }
        listeners1.clear()
        eventPriorityData.clear()
        ignoreCancelledData.clear()

        listeners2.forEach { listener -> MiniGameBuilderUtil(this).registerEvents(listener) }

    }

    private fun registerListener(listener: Listener) {
        listeners1[listener]?.let {
            MiniGameBuilderUtil(this).registerEvent(
                listeners1[listener]!!, listener, eventPriorityData[listener]!!, ignoreCancelledData[listener]!!
            )
        }
    }
}