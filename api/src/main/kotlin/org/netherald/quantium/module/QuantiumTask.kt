package org.netherald.quantium.module

abstract class QuantiumTask : Runnable {

    fun getTaskId(): Int {
        TODO()
    }

    fun getOwner(): QuantiumModule {
        TODO()
    }

    fun isSync(): Boolean {
        TODO()
    }

    fun isCancelled(): Boolean {
        TODO()
    }

    fun cancel() {
        TODO()
    }
}