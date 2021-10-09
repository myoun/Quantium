package org.netherald.quantium

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityEvent
import org.bukkit.event.entity.PlayerLeashEntityEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.weather.WeatherEvent
import org.bukkit.event.world.WorldEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

@QuantiumMarker
open class BuilderUtil(private val miniGameInstance: MiniGameInstance) {

    val players : List<Player> = miniGameInstance.players
    val worlds : List<String> = miniGameInstance.worlds

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
        listener: QuantiumEvent<T>.() -> Unit
    ) : Listener {
        if (!allServerEvent) {
            lateinit var listenerData : Listener
            if (PlayerEvent::class.java.isAssignableFrom(clazz)) {
                listenerData = listener0(clazz) {
                    val input = event as PlayerEvent
                    if (players.contains(input.player)) {
                        listener(this)
                    }
                }
            } else if (EntityEvent::class.java.isAssignableFrom(clazz)) {
                listenerData = listener0(clazz) {
                    val input = event as EntityEvent
                    if (worlds.contains(input.entity.world.name)) {
                        listener(this)
                    }
                }
            } else if (WorldEvent::class.java.isAssignableFrom(clazz)) {
                listenerData = listener0(clazz) {
                    @Suppress("CAST_NEVER_SUCCEEDS")
                    val input = this as WorldEvent
                    if (worlds.contains(input.world.name)) {
                        listener(this)
                    }
                }
            } else if (PlayerLeashEntityEvent::class.java.isAssignableFrom(clazz)) {
                listenerData = listener0(clazz) {
                    @Suppress("CAST_NEVER_SUCCEEDS", "CAST_NEVER_SUCCEEDS")
                    val input = this as PlayerLeashEntityEvent
                    if (players.contains(input.player)) {
                        listener(this)
                    }
                }
            } else if (WeatherEvent::class.java.isAssignableFrom(clazz)) {
                listenerData = listener0(clazz) {
                    @Suppress("CAST_NEVER_SUCCEEDS")
                    val input = this as WeatherEvent
                    if (worlds.contains(input.world.name)) {
                        listener(this)
                    }
                }
            } else {
                listenerData = listener0(clazz, listener)
            }
            return listenerData
        } else {
            return listener0(clazz, listener)
        }
    }

    private fun <T : Event> listener0(clazz : Class<T>, listener: QuantiumEvent<T>.() -> Unit) : Listener {
        val listenerData = object : Listener {
            @EventHandler
            fun on(event : T) {
                listener(QuantiumEvent(event, miniGameInstance))
            }
        }
        Bukkit.getServer().pluginManager.registerEvents(listenerData, miniGameInstance.miniGame.owner)
        miniGameInstance.listeners.add(listenerData)
        return listenerData
    }
}