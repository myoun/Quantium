package org.netherald.quantium.module.scheduler

import org.bukkit.scheduler.BukkitTask
import org.netherald.quantium.module.QuantiumModule

abstract class QuantiumRunnable(val owner : QuantiumModule) : Runnable {

    lateinit var task : QuantiumTask

    val bukkitTask : BukkitTask = task.bukkitTask
    val taskId : Int = task.taskId
    val isSync: Boolean = task.isSync
    val isCancelled: Boolean = task.isCancelled
    fun cancel() = task.cancel()

}