package org.netherald.quantium.module

import org.bukkit.scheduler.BukkitTask
import java.util.function.Consumer

class QuantiumScheduler {

    private val taskMap = HashMap<QuantiumModule, ArrayList<BukkitTask>>()

    private val QuantiumModule.tasks : ArrayList<BukkitTask>
    get() {
        taskMap[this] ?: run { taskMap[this] = ArrayList() }
        return taskMap[this]!!
    }

    private val taskMap2 = HashMap<BukkitTask, QuantiumModule>()

    private fun cancelTasks(module: QuantiumModule) {
        module.tasks.forEach {
            it.cancel()
        }
    }

    fun scheduleSyncDelayedTask(module: QuantiumModule, task: Runnable): Int {
        TODO()
    }

    fun runTask(module: QuantiumModule, runnable: Runnable): BukkitTask {
        TODO()
    }

    @Throws(IllegalArgumentException::class)
    fun runTask(module: QuantiumModule, task: Consumer<BukkitTask>) {
        TODO()
    }

    fun runTaskAsynchronously(module: QuantiumModule, runnable: Runnable): BukkitTask {
        TODO()
    }

    @Throws(IllegalArgumentException::class)
    fun runTaskAsynchronously(module: QuantiumModule, task: Consumer<BukkitTask>) {
        TODO()
    }

    fun scheduleSyncDelayedTask(module: QuantiumModule, task: Runnable, delay: Long): Int {
        TODO()
    }

    fun runTaskLater(module: QuantiumModule, runnable: Runnable, delay: Long): BukkitTask {
        TODO()
    }

    @Throws(IllegalArgumentException::class)
    fun runTaskLater(module: QuantiumModule, task: Consumer<BukkitTask>, delay: Long) {
        TODO()
    }

    fun runTaskLaterAsynchronously(module: QuantiumModule, runnable: Runnable, delay: Long): BukkitTask {
        TODO()
    }

    @Throws(IllegalArgumentException::class)
    fun runTaskLaterAsynchronously(module: QuantiumModule, task: Consumer<BukkitTask>, delay: Long) {
        TODO()
    }

    @Throws(IllegalArgumentException::class)
    fun runTaskTimerAsynchronously(module: QuantiumModule, task: Consumer<BukkitTask>, delay: Long, period: Long) {
        TODO()
    }

    fun scheduleSyncRepeatingTask(module: QuantiumModule, runnable: Runnable, delay: Long, period: Long): Int {
        TODO()
    }

    fun runTaskTimer(module: QuantiumModule, runnable: Runnable, delay: Long, period: Long): BukkitTask {
        TODO()
    }

    @Throws(IllegalArgumentException::class)
    fun runTaskTimer(module: QuantiumModule, task: Consumer<BukkitTask>, delay: Long, period: Long) {
        TODO()
    }

    fun scheduleInternalTask(run: Runnable, delay: Int, taskName: String?): BukkitTask {
        TODO()
    }

    fun runTaskTimerAsynchronously(module: QuantiumModule, runnable: Runnable, delay: Long, period: Long): BukkitTask {
        TODO()
    }
}