package org.netherald.quantium.util

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.*
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.entity.EntityEvent
import org.bukkit.event.entity.PlayerLeashEntityEvent
import org.bukkit.event.hanging.HangingEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.vehicle.VehicleEvent
import org.bukkit.event.weather.WeatherEvent
import org.bukkit.event.world.WorldEvent
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.IllegalPluginAccessException
import org.bukkit.plugin.RegisteredListener
import org.bukkit.plugin.TimedRegisteredListener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Scoreboard
import org.netherald.quantium.*
import org.netherald.quantium.event.AllServerEvent
import java.lang.reflect.InvocationTargetException

@QuantiumMarker
open class MiniGameBuilderUtil(private val miniGameInstance : MiniGameInstance) {

    var spawn : Location?
        get() = miniGameInstance.worldSetting.spawn
        set(value) { miniGameInstance.worldSetting.spawn = value }

    val players : Collection<Player> get() = miniGameInstance.players

    private val owner : JavaPlugin get() = miniGameInstance.miniGame.owner

    val worlds : Collection<World> get() = miniGameInstance.worlds

    val world : World? get() = miniGameInstance.world
    val worldNether : World? get() = miniGameInstance.worldNether
    val worldEnder : World? get() = miniGameInstance.worldEnder
    val otherWorlds : Collection<World> get() = miniGameInstance.otherWorlds

    val miniGame : MiniGame = miniGameInstance.miniGame
    fun createInstance() = miniGame.createInstance()

    fun addWorld(world: World, addWorldType: MiniGameInstance.AddWorldType) =
        miniGameInstance.addWorld(world, addWorldType)

    fun removeWorld(world: World) = miniGameInstance.removeWorld(world)

    fun applyScoreBoard(displayName : String, init : ScoreBoardBuilder.() -> Unit) =
        miniGameInstance.applyScoreBoard(displayName, init)

    var spectatorUtil : SpectatorUtil
        get() = miniGameInstance.spectatorUtil
        set(value) { miniGameInstance.spectatorUtil = value }

    fun stopGame() = miniGameInstance.stopGame()
    fun delete() = miniGameInstance.delete()

    fun teamMatch() = miniGameInstance.teamSetting.teamMatcher.match(players)

    val team : List<List<Player>> get() = miniGameInstance.team

    fun broadCast(message : String) = miniGameInstance.broadcast(message)

    fun Player.applySpectator() { spectatorUtil.applySpectator(this) }
    fun Player.unApplySpectator() { spectatorUtil.unApplySpectator(this) }

    fun <T> loopTask(
        range: Iterable<T>, delay : Long = 1, period : Long = 1, task : MiniGameInstanceTask.(T) -> Unit
    ) : MiniGameInstanceTask {
        return loopTask(delay, period, range.iterator(), task)
    }

    fun <T> loopTask(
        delay : Long = 1, period : Long = 1, collection: Collection<T>, task : MiniGameInstanceTask.(T) -> Unit
    ) : MiniGameInstanceTask {
        return loopTask(delay, period, collection.iterator(), task)
    }

    fun <T> loopTask(
        delay : Long = 1, period : Long = 1, iterator: Iterator<T>, task : MiniGameInstanceTask.(T) -> Unit
    ) : MiniGameInstanceTask {
        return loopTask(delay, period) { if (iterator.hasNext()) task(iterator.next()) else cancel() }
    }

    fun loopTask(
        delay : Long = 1, period : Long = 1, task : MiniGameInstanceTask.() -> Unit
    ) : MiniGameInstanceTask {
        val taskData = MiniGameInstanceTask(miniGameInstance)
        val runnable : BukkitRunnable = object : BukkitRunnable() { override fun run() { taskData.task() } }
        taskData.task = runnable.runTaskTimer(owner, delay, period)
        miniGameInstance.tasks.add(taskData)
        return taskData
    }

    fun <T> asyncLoopTask(
        range: Iterable<T>, delay : Long = 1, period : Long = 1, task : MiniGameInstanceTask.(T) -> Unit
    ) : MiniGameInstanceTask {
        return asyncLoopTask(delay, period, range.iterator(), task)
    }

    fun <T> asyncLoopTask(
        delay : Long = 1, period : Long = 1, collection: Collection<T>, task : MiniGameInstanceTask.(T) -> Unit
    ) : MiniGameInstanceTask {
        return asyncLoopTask(delay, period, collection.iterator(), task)
    }

    fun <T> asyncLoopTask(
        delay : Long = 1, period : Long = 1, iterator: Iterator<T>, task : MiniGameInstanceTask.(T) -> Unit
    ) : MiniGameInstanceTask {
        return asyncLoopTask(delay, period) { if (iterator.hasNext()) task(this, iterator.next()) else cancel() }
    }

    fun asyncLoopTask(delay : Long = 0, period : Long = 0, task : MiniGameInstanceTask.() -> Unit) : MiniGameInstanceTask {
        val taskData = MiniGameInstanceTask(miniGameInstance)
        val runnable = object : BukkitRunnable() { override fun run() { task(taskData) } }
        taskData.task = runnable.runTaskTimerAsynchronously(owner, delay, period)
        miniGameInstance.tasks.add(taskData)
        return taskData
    }

    fun runTaskLater(delay : Long, task : MiniGameInstanceTask.() -> Unit) : MiniGameInstanceTask {
        val taskData = MiniGameInstanceTask(miniGameInstance)
        val runnable = object : BukkitRunnable() {
            override fun run() {
                taskData.task()
                miniGameInstance.tasks.remove(taskData)
            }
        }
        taskData.task = runnable.runTaskLater(owner, delay)
        miniGameInstance.tasks.add(taskData)
        return taskData
    }

    fun runTaskLaterAsync(delay : Long, task : MiniGameInstanceTask.() -> Unit) : MiniGameInstanceTask {
        val taskData = MiniGameInstanceTask(miniGameInstance)
        val runnable = object : BukkitRunnable() {
            override fun run() {
                taskData.task()
                miniGameInstance.tasks.remove(taskData)
            }
        }
        taskData.task = runnable.runTaskLaterAsynchronously(owner, delay)
        miniGameInstance.tasks.add(taskData)
        return taskData
    }

    /*
      if allPlayer is true
      listening all player's event
      if allPlayer is false
      listening only MiniGame's players event or
      listening only MiniGame's world's event
     */

    fun <T : Event> listener(
        clazz : Class<out T>,
        allServerEvent : Boolean = false,
        eventPriority : EventPriority = EventPriority.NORMAL,
        ignoreCancelled : Boolean = false,
        register: Boolean = true,
        listener: QuantiumEvent<out T>.() -> Unit
    ) : Listener {
        return if (!allServerEvent) {
            listener0(clazz, eventPriority, ignoreCancelled, register) {
                this@MiniGameBuilderUtil.listenerFilter(event) {
                    listener()
                }
            }
        } else {
            listener0(clazz, eventPriority, ignoreCancelled, register, listener)
        }
    }

    fun registerEvents(listener: Listener) {
        listener.javaClass.methods.forEach {
            val clazz = it.javaClass
            if (it.isBridge || it.isSynthetic) return@forEach
            val handler = it.getAnnotation(EventHandler::class.java) ?: return@forEach
            if (clazz.typeParameters.size != 1) {
                owner.logger.severe(
                    owner.description
                        .fullName + " attempted to register an invalid EventHandler method signature \""
                            + it.toGenericString() + "\" in " + listener.javaClass
                )
                return@forEach
            }
            val classAsEvent = clazz.typeParameters[0].javaClass.asSubclass(Event::class.java)
            lateinit var eventExecutor : EventExecutor
            it.getAnnotation(AllServerEvent::class.java)?.let { _ ->
                eventExecutor = eventExecutor(classAsEvent) { listener, event -> it.invoke(listener, event) }
            } ?: run {
                eventExecutor = eventExecutor(classAsEvent) { listener, event ->
                    listenerFilter(event) {
                        it.invoke(listener, event)
                    }
                }
            }
            registerRegisteredListener(
                classAsEvent, listener, eventExecutor, handler.priority, handler.ignoreCancelled
            )
        }
    }







    private fun <T : Event> listener0(
        clazz : Class<out T>,
        eventPriority : EventPriority = EventPriority.NORMAL,
        ignoreCancelled : Boolean = false,
        register : Boolean = true,
        listener: QuantiumEvent<out T>.() -> Unit
    ) : Listener {
        val listenerData = object : Listener {
            @EventHandler
            fun on(event : T) {
                listener(QuantiumEvent(event, miniGameInstance))
            }
        }
        if (register) {
            registerEvent(clazz, listenerData, eventPriority, ignoreCancelled)
            miniGameInstance.listeners.add(listenerData)
        }
        return listenerData
    }

    fun registerEvent(
        clazz: Class<out Event>,
        listenerData: Listener,
        priority: EventPriority,
        ignoreCancelled: Boolean
    ) {
        val eventExecutor = eventExecutor(clazz) { listener, event ->
            listenerFilter(event) {
                listenerData.javaClass.methods[0].invoke(listener, event)
            }
        }
        registerRegisteredListener(
            clazz, listenerData, eventExecutor, priority, ignoreCancelled
        )
    }

    private fun eventExecutor(
        clazz: Class<out Event>, listenerData: (listener : Listener, event : Event) -> Unit
    ) : EventExecutor {
        return EventExecutor { listener, event ->
            kotlin.runCatching {
                if (!clazz.isAssignableFrom(event.javaClass)) { return@EventExecutor }
                listenerData(listener, event)
            }.exceptionOrNull()?.let {
                if (it is InvocationTargetException) throw EventException(it.cause)
                throw EventException(it)
            }
        }
    }

    private fun registerRegisteredListener(
        clazz: Class<out Event>,
        listener: Listener,
        executor: EventExecutor,
        priority: EventPriority,
        ignoreCancelled: Boolean
    ) {
        if (Bukkit.getServer().pluginManager.useTimings()) {
            clazz.handlerList.register(
                TimedRegisteredListener(
                    listener,
                    executor,
                    priority,
                    owner,
                    ignoreCancelled
                )
            )
        } else {
            clazz.handlerList.register(RegisteredListener(
                listener, executor, priority, owner, ignoreCancelled)
            )
        }
    }

    private val Class<out Event>.handlerList : HandlerList
        get() {
            val throwException = fun (): Nothing = throw IllegalPluginAccessException(
                "Unable to find handler list for event ${name}. Static getHandlerList method required!"
            )

            val throwableHandlerList = fun Class<out Event>.() : HandlerList {
                val method = getDeclaredMethod("getHandlerList").apply { isAccessible = true }
                return method.invoke(null) as HandlerList
            }
            var nowClass : Class<out Event> = this
            kotlin.runCatching { return nowClass.throwableHandlerList() }
            while (true) {
                kotlin.runCatching { return nowClass.throwableHandlerList() }
                nowClass.superclass ?: throwException()
                if (nowClass.superclass == Event::class.java) throwException()
                nowClass = nowClass.superclass as Class<out Event>
            }
        }

    fun listenerFilter(event : Event, code : () -> Unit) { if (isMiniGameEvent(event)) { code() } }

    fun isMiniGameEvent(event : Event) : Boolean {
        val clazz = event::class.java
        return if (PlayerEvent::class.java.isAssignableFrom(clazz)) {
            val input = event as PlayerEvent
            this.miniGameInstance.players.contains(input.player)
        } else if (EntityEvent::class.java.isAssignableFrom(clazz)) {
            val input = event as EntityEvent
            this.miniGameInstance.worlds.contains(input.entity.world)
        } else if (WorldEvent::class.java.isAssignableFrom(clazz)) {
            @Suppress("CAST_NEVER_SUCCEEDS")
            val input = this as WorldEvent
            this.miniGameInstance.worlds.contains(input.world)
        } else if (PlayerLeashEntityEvent::class.java.isAssignableFrom(clazz)) {
            @Suppress("CAST_NEVER_SUCCEEDS")
            val input = this as PlayerLeashEntityEvent
            this.miniGameInstance.players.contains(input.player)
        } else if (WeatherEvent::class.java.isAssignableFrom(clazz)) {
            @Suppress("CAST_NEVER_SUCCEEDS")
            val input = this as WeatherEvent
            this.miniGameInstance.worlds.contains(input.world)
        } else if (VehicleEvent::class.java.isAssignableFrom(clazz)) {
            @Suppress("CAST_NEVER_SUCCEEDS")
            val input = this as VehicleEvent
            this.miniGameInstance.worlds.contains(input.vehicle.world)
        } else if (BlockEvent::class.java.isAssignableFrom(clazz)) {
            @Suppress("CAST_NEVER_SUCCEEDS")
            val input = this as BlockEvent
            this.miniGameInstance.worlds.contains(input.block.world)
        } else if (HangingEvent::class.java.isAssignableFrom(clazz)) {
            @Suppress("CAST_NEVER_SUCCEEDS")
            val input = this as HangingEvent
            this.miniGameInstance.worlds.contains(input.entity.world)
        } else {
            false
        }
    }
}