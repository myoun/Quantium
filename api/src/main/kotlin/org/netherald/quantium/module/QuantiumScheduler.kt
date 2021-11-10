package org.netherald.quantium.module

import java.util.function.Consumer

class QuantiumScheduler {

    private val taskMap = HashMap<QuantiumModule, ArrayList<QuantiumTask>>()

    private val QuantiumModule.tasks : ArrayList<QuantiumTask>
    get() {
        taskMap[this] ?: run { taskMap[this] = ArrayList() }
        return taskMap[this]!!
    }

    fun cancelTasks(module: QuantiumModule) {
        module.tasks.forEach {
            it.cancel()
        }
    }

    fun cancelTask(task : QuantiumTask) {
        task.cancel()
    }

    fun scheduleSyncDelayedTask(module: QuantiumModule, task: QuantiumRunnable): Int {
        TODO()
    }

    fun runTask(module: QuantiumModule, runnable: QuantiumRunnable): QuantiumTask {
        TODO()
    }

    @Throws(IllegalArgumentException::class)
    fun runTask(module: QuantiumModule, task: Consumer<QuantiumTask>) {
        TODO()
    }

    fun runTaskAsynchronously(module: QuantiumModule, runnable: QuantiumRunnable): QuantiumTask {
        TODO()
    }

    @Throws(IllegalArgumentException::class)
    fun runTaskAsynchronously(module: QuantiumModule, task: Consumer<QuantiumTask>) {
        TODO()
    }

    fun scheduleSyncDelayedTask(module: QuantiumModule, task: QuantiumRunnable, delay: Long): Int {
        TODO()
    }

    fun runTaskLater(module: QuantiumModule, runnable: QuantiumRunnable, delay: Long): QuantiumTask {
        TODO()
    }

    @Throws(IllegalArgumentException::class)
    fun runTaskLater(module: QuantiumModule, task: Consumer<QuantiumTask>, delay: Long) {
        TODO()
    }

    fun runTaskLaterAsynchronously(module: QuantiumModule, runnable: QuantiumRunnable, delay: Long): QuantiumTask {
        TODO()
    }

    @Throws(IllegalArgumentException::class)
    fun runTaskLaterAsynchronously(module: QuantiumModule, task: Consumer<QuantiumTask>, delay: Long) {
        TODO()
    }

    @Throws(IllegalArgumentException::class)
    fun runTaskTimerAsynchronously(module: QuantiumModule, task: Consumer<QuantiumTask>, delay: Long, period: Long) {
        TODO()
    }

    fun scheduleSyncRepeatingTask(module: QuantiumModule, runnable: QuantiumRunnable, delay: Long, period: Long): Int {
        TODO()
    }

    fun runTaskTimer(module: QuantiumModule, runnable: QuantiumRunnable, delay: Long, period: Long): QuantiumTask {
        TODO()
    }

    @Throws(IllegalArgumentException::class)
    fun runTaskTimer(module: QuantiumModule, task: Consumer<QuantiumTask>, delay: Long, period: Long) {
        TODO()
    }

    fun scheduleInternalTask(run: QuantiumRunnable, delay: Int, taskName: String): QuantiumTask {
        TODO()
    }

    fun runTaskTimerAsynchronously(module: QuantiumModule, runnable: QuantiumRunnable, delay: Long, period: Long): QuantiumTask {
        TODO()
    }
}