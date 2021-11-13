package org.netherald.quantium.util

interface ServerUtil {
    companion object {
        lateinit var default : ServerUtil
    }
    val isBlocked : Boolean
    fun setBlockServer(value: Boolean)
}