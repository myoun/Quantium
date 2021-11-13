package org.netherald.quantium.util

interface ServerUtil {
    companion object {
        var default : ServerUtil? = null
    }
    val isBlocked : Boolean
    fun setBlockServer(value: Boolean)

    val miniGames : Collection<String>
}