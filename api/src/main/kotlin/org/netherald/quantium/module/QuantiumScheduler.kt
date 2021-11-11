package org.netherald.quantium.module

import org.bukkit.Bukkit
import org.netherald.quantium.Quantium

class QuantiumScheduler {

    val taskMap = HashMap<QuantiumModule, ArrayList<QuantiumTask>>()
    private val taskMap2 = HashMap<QuantiumTask, QuantiumModule>()
    private val scheduler = Bukkit.getScheduler()

    fun cancelTasks(module: QuantiumModule) {
        module.tasks.forEach {
            it.cancel()
        }
    }

    fun cancelTask(task : QuantiumTask) {
        task.cancel()
        val module = taskMap2[task]!!
        module.removeTask(task)
    }

    private fun QuantiumModule.removeTask(task: QuantiumTask) {
        tasks as ArrayList<QuantiumTask> -= task
    }

    private fun QuantiumModule.addTask(task : QuantiumTask) {
        tasks as ArrayList<QuantiumTask> += task
    }

    fun runTask(module: QuantiumModule, runnable: QuantiumRunnable): QuantiumTask {
        val task = QuantiumTask(
            scheduler.runTask(Quantium.plugin, RunnableConverter2(runnable)),
            module
        )
        runnable.task = task
        module.addTask(task)
        return task
    }

    fun runTaskAsynchronously(module: QuantiumModule, runnable: QuantiumRunnable): QuantiumTask {
        val task = QuantiumTask(
            scheduler.runTaskAsynchronously(Quantium.plugin, RunnableConverter2(runnable)),
            module
        )
        runnable.task = task
        module.addTask(task)
        return task
    }

    fun runTaskLater(module: QuantiumModule, runnable: QuantiumRunnable, delay: Long): QuantiumTask {
        val task = QuantiumTask(
            scheduler.runTaskLater(Quantium.plugin, RunnableConverter2(runnable), delay),
            module
        )
        runnable.task = task
        module.addTask(task)
        return task
    }

    fun runTaskLaterAsynchronously(
        module: QuantiumModule, runnable: QuantiumRunnable, delay: Long,
    ): QuantiumTask {
        val task = QuantiumTask(
            scheduler.runTaskLaterAsynchronously(Quantium.plugin, RunnableConverter2(runnable), delay),
            module
        )
        runnable.task = task
        module.addTask(task)
        return task
    }

    fun runTaskTimer(module: QuantiumModule, runnable: QuantiumRunnable, delay: Long, period: Long = 0): QuantiumTask {
        val task = QuantiumTask(
            scheduler.runTaskTimer(Quantium.plugin, RunnableConverter2(runnable), delay, period), module
        )
        runnable.task = task
        module.addTask(task)
        return task
    }

    fun runTaskTimerAsynchronously(
        module: QuantiumModule, runnable: QuantiumRunnable, delay: Long, period: Long = 0
    ): QuantiumTask {
        val task = QuantiumTask(
            scheduler.runTaskTimerAsynchronously(Quantium.plugin, RunnableConverter2(runnable), delay, period),
            module
        )
        runnable.task = task
        module.addTask(task)
        return task
    }

    fun <T> loopTask(
        module: QuantiumModule,
        range: Iterable<T>, delay : Long = 1, period : Long = 1, task : QuantiumTask.(T) -> Unit
    ) : QuantiumTask {
        return loopTask(module, delay, period, range.iterator(), task)
    }

    fun <T> loopTask(
        module: QuantiumModule,
        delay : Long = 1,
        period : Long = 1,
        collection: Collection<T>,
        task : QuantiumTask.(T) -> Unit
    ) : QuantiumTask {
        return loopTask(module, delay, period, collection.iterator(), task)
    }

    fun <T> loopTask(
        module: QuantiumModule,
        delay : Long = 1,
        period : Long = 1,
        iterator: Iterator<T>,
        task : QuantiumTask.(T) -> Unit
    ) : QuantiumTask {
        return loopTask(module, delay, period) { if (iterator.hasNext()) task(iterator.next()) else cancel() }
    }

    fun loopTask(
        module: QuantiumModule,
        delay : Long = 1,
        period : Long = 1,
        task : QuantiumTask.() -> Unit
    ) : QuantiumTask = runTaskTimer(module, RunnableConverter(module, task), delay, period)

    fun <T> asyncLoopTask(
        module: QuantiumModule,
        range: Iterable<T>,
        delay : Long = 1,
        period : Long = 1,
        task : QuantiumTask.(T) -> Unit
    ) : QuantiumTask = asyncLoopTask(module, delay, period, range.iterator(), task)

    fun <T> asyncLoopTask(
        module: QuantiumModule,
        delay : Long = 1,
        period : Long = 1,
        collection: Collection<T>,
        task : QuantiumTask.(T) -> Unit
    ) : QuantiumTask = asyncLoopTask(module, delay, period, collection.iterator(), task)

    fun <T> asyncLoopTask(
        module: QuantiumModule,
        delay : Long = 1,
        period : Long = 1,
        iterator: Iterator<T>,
        task : QuantiumTask.(T) -> Unit
    ) : QuantiumTask =
        asyncLoopTask(module, delay, period) { if (iterator.hasNext()) task(this, iterator.next()) else cancel() }

    fun asyncLoopTask(
        module: QuantiumModule,
        delay : Long = 0,
        period : Long = 0,
        task : QuantiumTask.() -> Unit
    ) : QuantiumTask = runTaskTimerAsynchronously(module, RunnableConverter(module, task), delay, period)

    fun runTaskLater(
        module: QuantiumModule,
        delay : Long,
        task : QuantiumTask.() -> Unit
    ) : QuantiumTask = runTaskLater(module, RunnableConverter(module, task), delay)

    fun runTaskLaterAsync(
        module: QuantiumModule,
        delay : Long,
        task : QuantiumTask.() -> Unit
    ) : QuantiumTask = runTaskLaterAsynchronously(module, RunnableConverter(module, task), delay)

    inner class RunnableConverter(
        module: QuantiumModule,
        val code :  QuantiumTask.() -> Unit
    ) : QuantiumRunnable(module) {
        override fun run() {
            task.code()
        }
    }

    inner class RunnableConverter2(
        val runnable: QuantiumRunnable
    ) : Runnable {
        override fun run() {
            runnable.run()
        }
    }
}