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

    fun addWorld(world: World, addWorldType: MiniGameInstance.AddWorldType = MiniGameInstance.AddWorldType.OTHER) =
        miniGameInstance.addWorld(world, addWorldType)

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
            try {
                if (!clazz.isAssignableFrom(event.javaClass)) {
                    return@EventExecutor
                }
                listenerData.javaClass.declaredMethods[0].invoke(listener, event)
            } catch (ex: InvocationTargetException) {
                throw EventException(ex.cause)
            } catch (t: Throwable) {
                throw EventException(t)
            }
        }
        if (Bukkit.getServer().pluginManager.useTimings()) {
            getHandlerList(clazz).register(
                TimedRegisteredListener(
                    listenerData,
                    executor,
                    priority,
                    owner,
                    ignoreCancelled
                )
            )
        } else {
            getHandlerList(clazz).register(
                RegisteredListener(listenerData, executor, priority, owner, ignoreCancelled)
            )
        }
    }
    //
    private fun getHandlerList(
        clazz: Class<out Event>
    ) : HandlerList {
        val throwException = fun (): Nothing = throw IllegalPluginAccessException(
            "Unable to find handler list for event " + clazz.name + ". Static getHandlerList method required!"
        )
        try {
            val method = clazz.getDeclaredMethod("getHandlerList").apply { isAccessible = true }
            @Suppress("CAST_NEVER_SUCCEEDS")
            return method.invoke(null) as HandlerList
        } catch (e : NoSuchMethodException) {
            clazz.superclass?.let {
                if (it.superclass == Event::class.java) throwException()
                @Suppress("UNCHECKED_CAST")
                return getHandlerList(it as Class<out Event>)
                // ```````````````````믕
            } ?: run {
                throwException()
            }
        }
    }
}