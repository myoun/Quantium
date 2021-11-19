package org.netherald.quantium.util

import java.util.*

interface ServerUtil {
    companion object {
        var default : ServerUtil? = null
    }
    val isBlocked : Boolean
    fun setBlockServer(value: Boolean)

    val miniGames : Collection<String>

    fun getInstances(game : String) : Collection<UUID>?
    val instances : Collection<UUID>
}