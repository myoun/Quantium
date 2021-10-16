package org.netherald.quantium

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.entity.EntityEvent
import org.bukkit.event.entity.PlayerLeashEntityEvent
import org.bukkit.event.hanging.HangingEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.vehicle.VehicleEvent
import org.bukkit.event.weather.WeatherEvent
import org.bukkit.event.world.WorldEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

@QuantiumMarker
open class BuilderUtil(private val miniGameInstance: MiniGameInstance) {

    fun broadCast(message : String) = miniGameInstance.broadcast(message)

    fun loopTask(delay : Long = 0, period : Long = 0, task : () -> Unit) : BukkitTask {
        var taskData : BukkitTask? = null
        val runnable = object : BukkitRunnable() {
            override fun run() {
                task()
                miniGameInstance.tasks.remove(taskData)
            }
        }
        taskData = runnable.runTaskTimer(miniGameInstance.miniGame.owner, delay, period)

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
        taskData = runnable.runTaskTimerAsynchronously(miniGameInstance.miniGame.owner, delay, period)
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
        taskData = runnable.runTaskLater(miniGameInstance.miniGame.owner, delay)
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
        taskData = runnable.runTaskLaterAsynchronously(miniGameInstance.miniGame.owner, delay)
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
        clazz : Class<T>,
        allServerEvent : Boolean = false,
        register: Boolean = true,
        listener: QuantiumEvent<T>.() -> Unit
    ) : Listener {
        if (!allServerEvent) {
            lateinit var listenerData : Listener
            if (PlayerEvent::class.java.isAssignableFrom(clazz)) {
                listenerData = listener0(clazz, register) {
                    val input = event as PlayerEvent
                    if (this@BuilderUtil.miniGameInstance.players.contains(input.player)) {
                        listener(this)
                    }
                }
            } else if (EntityEvent::class.java.isAssignableFrom(clazz)) {
                listenerData = listener0(clazz, register) {
                    val input = event as EntityEvent
                    if (this@BuilderUtil.miniGameInstance.worlds.contains(input.entity.world)) {
                        listener(this)
                    }
                }
            } else if (WorldEvent::class.java.isAssignableFrom(clazz)) {
                listenerData = listener0(clazz, register) {
                    @Suppress("CAST_NEVER_SUCCEEDS")
                    val input = this as WorldEvent
                    if (this@BuilderUtil.miniGameInstance.worlds.contains(input.world)) {
                        listener(this)
                    }
                }
            } else if (PlayerLeashEntityEvent::class.java.isAssignableFrom(clazz)) {
                listenerData = listener0(clazz, register) {
                    @Suppress("CAST_NEVER_SUCCEEDS")
                    val input = this as PlayerLeashEntityEvent
                    if (this@BuilderUtil.miniGameInstance.players.contains(input.player)) {
                        listener(this)
                    }
                }
            } else if (WeatherEvent::class.java.isAssignableFrom(clazz)) {
                listenerData = listener0(clazz,register) {
                    @Suppress("CAST_NEVER_SUCCEEDS")
                    val input = this as WeatherEvent
                    if (this@BuilderUtil.miniGameInstance.worlds.contains(input.world)) {
                        listener(this)
                    }
                }
            } else if (VehicleEvent::class.java.isAssignableFrom(clazz)) {
                listenerData = listener0(clazz, register) {
                    @Suppress("CAST_NEVER_SUCCEEDS")
                    val input = this as VehicleEvent
                    if (this@BuilderUtil.miniGameInstance.worlds.contains(input.vehicle.world)) {
                        listener(this)
                    }
                }
            } else if (BlockEvent::class.java.isAssignableFrom(clazz)) {
                listenerData = listener0(clazz, register) {
                    @Suppress("CAST_NEVER_SUCCEEDS")
                    val input = this as BlockEvent
                    if (this@BuilderUtil.miniGameInstance.worlds.contains(input.block.world)) {
                        listener(this)
                    }
                }
            } else if (HangingEvent::class.java.isAssignableFrom(clazz)) {
                listenerData = listener0(clazz, register) {
                    @Suppress("CAST_NEVER_SUCCEEDS")
                    val input = this as HangingEvent
                    if (this@BuilderUtil.miniGameInstance.worlds.contains(input.entity.world)) {
                        listener(this)
                    }
                }
            } else {
                listenerData = listener0(clazz, register, listener)
            }
            return listenerData
        } else {
            return listener0(clazz, register, listener)
        }
    }

    private fun <T : Event> listener0(
        clazz : Class<T>,
        register : Boolean = true,
        listener: QuantiumEvent<T>.() -> Unit
    ) : Listener {
        val listenerData = object : Listener {
            @EventHandler
            fun on(event : T) {
                listener(QuantiumEvent(event, miniGameInstance))
            }
        }
        if (register) {
            Bukkit.getServer().pluginManager.registerEvents(listenerData, miniGameInstance.miniGame.owner)
            miniGameInstance.listeners.add(listenerData)
        }
        return listenerData
    }
}