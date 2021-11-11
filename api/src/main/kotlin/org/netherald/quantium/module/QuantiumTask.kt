package org.netherald.quantium.module

import org.bukkit.scheduler.BukkitTask
import org.netherald.quantium.Quantium

class QuantiumTask(val bukkitTask : BukkitTask, val owner : QuantiumModule) {

    val taskId : Int = bukkitTask.taskId
    val isSync: Boolean = bukkitTask.isSync
    val isCancelled: Boolean = bukkitTask.isCancelled

    fun cancel() {
        bukkitTask.cancel()
        owner.tasks as ArrayList<QuantiumTask> -= this
    }
}