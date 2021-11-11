package org.netherald.quantium.module

import org.bukkit.scheduler.BukkitTask

class QuantiumTask(val bukkitTask : BukkitTask, val owner : QuantiumModule) {

    val taskId : Int = bukkitTask.taskId
    val isSync: Boolean = bukkitTask.isSync
    val isCancelled: Boolean = bukkitTask.isCancelled
    fun cancel() = bukkitTask.cancel()

}