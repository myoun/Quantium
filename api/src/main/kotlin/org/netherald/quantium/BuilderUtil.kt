package org.netherald.quantium

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
import org.bukkit.scheduler.BukkitTask
import org.netherald.quantium.util.SpectatorUtil
import java.lang.reflect.InvocationTargetException

@QuantiumMarker
open class BuilderUtil(private val miniGameInstance: MiniGameInstance) {

    var spawn : Location?
        get() = miniGameInstance.worldSetting.spawn
        set(value) { miniGameInstance.worldSetting.spawn = value }

    val players : Collection<Player>
        get() = miniGameInstance.players

    private val owner : JavaPlugin
        get() = miniGameInstance.miniGame.owner

    val worlds : Collection<World>
        get() = miniGameInstance.worlds

    val world : World?
        get() = miniGameInstance.world
    val worldNether : World?
        get() = miniGameInstance.worldNether
    val worldEnder : World?
        get() = miniGameInstance.worldEnder
    val otherWorlds : Collection<World>
        get() = miniGameInstance.otherWorlds


    fun addWorld(world: World, addWorldType: MiniGameInstance.AddWorldType) =
        miniGameInstance.addWorld(world, addWorldType)

    fun remove(world: World) = miniGameInstance.removeWorld(world)

    var spectatorUtil : SpectatorUtil
        get() = miniGameInstance.spectatorUtil
        set(value) { miniGameInstance.spectatorUtil = value }

    fun stopGame() = miniGameInstance.stopGame()
    fun delete() = miniGameInstance.delete()

    fun teamMatch() = miniGameInstance.teamSetting.teamMatcher.match(players)

    val team : List<List<Player>>
        get() = miniGameInstance.team

    fun broadCast(message : String) = miniGameInstance.broadcast(message)

    fun Player.applySpectator() {
        spectatorUtil.applySpectator(this)
    }

    fun Player.unApplySpectator() {
        spectatorUtil.unApplySpectator(this)
    }

    fun loopTask(delay : Long = 0, period : Long = 0, task : () -> Unit) : BukkitTask {
        var taskData : BukkitTask? = null
        val runnable = object : BukkitRunnable() {
            override fun run() {
                task()
                miniGameInstance.tasks.remove(taskData)
            }
        }
        taskData = runnable.runTaskTimer(owner, delay, period)

        miniGameInstance.tasks.add(taskData)
        return taskData
    }

    fun asyncLoopTask(delay : Long = 0, period : Long = 0, task : () -> Unit) : BukkitTask {
        var taskData : BukkitTask? = null
        val runnable = object : BukkitRunnable() {
            override fun run() {
                task()
                miniGameInstance.tasks.remove(taskData)
            }
        }
        taskData = runnable.runTaskTimerAsynchronously(owner, delay, period)
        miniGameInstance.tasks.add(taskData)
        return taskData
    }

    fun runTaskLater(delay : Long, task : () -> Unit) : BukkitTask {
        var taskData : BukkitTask? = null
        val runnable = object : BukkitRunnable() {
            override fun run() {
                task()
                miniGameInstance.tasks.remove(taskData)
            }
        }
        taskData = runnable.runTaskLater(owner, delay)
        miniGameInstance.tasks.add(taskData)
        return taskData
    }

    fun runTaskLaterAsync(delay : Long, task : () -> Unit) : BukkitTask {
        var taskData : BukkitTask? = null
        val runnable = object : BukkitRunnable() {
            override fun run() {
                task()
                miniGameInstance.tasks.remove(taskData)
            }
        }
        taskData = runnable.runTaskLaterAsynchronously(owner, delay)
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
        if (!allServerEvent) {
            lateinit var listenerData : Listener
            if (PlayerEvent::class.java.isAssignableFrom(clazz)) {
                listenerData = listener0(clazz, eventPriority, ignoreCancelled, register) {
                    val input = event as PlayerEvent
                    if (this@BuilderUtil.miniGameInstance.players.contains(input.player)) {
                        listener(this)
                    }
                }
            } else if (EntityEvent::class.java.isAssignableFrom(clazz)) {
                listenerData = listener0(clazz, eventPriority, ignoreCancelled, register) {
                    val input = event as EntityEvent
                    if (this@BuilderUtil.miniGameInstance.worlds.contains(input.entity.world)) {
                        listener(this)
                    }
                }
            } else if (WorldEvent::class.java.isAssignableFrom(clazz)) {
                listenerData = listener0(clazz, eventPriority, ignoreCancelled, register) {
                    @Suppress("CAST_NEVER_SUCCEEDS")
                    val input = this as WorldEvent
                    if (this@BuilderUtil.miniGameInstance.worlds.contains(input.world)) {
                        listener(this)
                    }
                }
            } else if (PlayerLeashEntityEvent::class.java.isAssignableFrom(clazz)) {
                listenerData = listener0(clazz, eventPriority, ignoreCancelled, register) {
                    @Suppress("CAST_NEVER_SUCCEEDS")
                    val input = this as PlayerLeashEntityEvent
                    if (this@BuilderUtil.miniGameInstance.players.contains(input.player)) {
                        listener(this)
                    }
                }
            } else if (WeatherEvent::class.java.isAssignableFrom(clazz)) {
                listenerData = listener0(clazz, eventPriority, ignoreCancelled, register) {
                    @Suppress("CAST_NEVER_SUCCEEDS")
                    val input = this as WeatherEvent
                    if (this@BuilderUtil.miniGameInstance.worlds.contains(input.world)) {
                        listener(this)
                    }
                }
            } else if (VehicleEvent::class.java.isAssignableFrom(clazz)) {
                listenerData = listener0(clazz, eventPriority, ignoreCancelled, register) {
                    @Suppress("CAST_NEVER_SUCCEEDS")
                    val input = this as VehicleEvent
                    if (this@BuilderUtil.miniGameInstance.worlds.contains(input.vehicle.world)) {
                        listener(this)
                    }
                }
            } else if (BlockEvent::class.java.isAssignableFrom(clazz)) {
                listenerData = listener0(clazz, eventPriority, ignoreCancelled, register) {
                    @Suppress("CAST_NEVER_SUCCEEDS")
                    val input = this as BlockEvent
                    if (this@BuilderUtil.miniGameInstance.worlds.contains(input.block.world)) {
                        listener(this)
                    }
                }
            } else if (HangingEvent::class.java.isAssignableFrom(clazz)) {
                listenerData = listener0(clazz, eventPriority, ignoreCancelled, register) {
                    @Suppress("CAST_NEVER_SUCCEEDS")
                    val input = this as HangingEvent
                    if (this@BuilderUtil.miniGameInstance.worlds.contains(input.entity.world)) {
                        listener(this)
                    }
                }
            } else {
                listenerData = listener0(clazz, eventPriority, ignoreCancelled, register, listener)
            }
            return listenerData
        } else {
            return listener0(clazz, eventPriority, ignoreCancelled, register, listener)
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
        val executor = EventExecutor { listener, event ->
            kotlin.runCatching {
                if (!clazz.isAssignableFrom(event.javaClass)) {
                    return@EventExecutor
                }
                listenerData.javaClass.declaredMethods[0].invoke(listener, event)
            }.exceptionOrNull()?.let {
                if (it is InvocationTargetException) throw EventException(it.cause)
                throw EventException(it)
            }
        }
        if (Bukkit.getServer().pluginManager.useTimings()) {
            clazz.handlerList.register(
                TimedRegisteredListener(
                    listenerData,
                    executor,
                    priority,
                    owner,
                    ignoreCancelled
                )
            )
        } else {
            clazz.handlerList.register(
                RegisteredListener(listenerData, executor, priority, owner, ignoreCancelled)
            )
        }
    }

    private val Class<out Event>.handlerList : HandlerList
        get() {
            val throwException = fun (): Nothing = throw IllegalPluginAccessException(
                "Unable to find handler list for event ${name}. Static getHandlerList method required!"
            )

            val exceptionableHandlerList = fun Class<out Event>.() : HandlerList {
                val method = getDeclaredMethod("getHandlerList").apply { isAccessible = true }
                return method.invoke(null) as HandlerList
            }
            var nowClass : Class<out Event> = this
            kotlin.runCatching { return nowClass.exceptionableHandlerList() }
            while (true) {
                kotlin.runCatching { return nowClass.exceptionableHandlerList() }
                nowClass.superclass ?: throwException()
                if (nowClass.superclass == Event::class.java) throwException()
                nowClass = nowClass.superclass as Class<out Event>
            }
        }
}