package org.netherald.quantium.dataclass

import org.bukkit.entity.Player
import org.netherald.quantium.MiniGameInstance

object MiniGameInstanceData {
    val miniGameReJoinData = HashMap<MiniGameInstance, List<Player>>()
}

val MiniGameInstance.rejoinData : List<Player>
    get() {
        MiniGameInstanceData.miniGameReJoinData[this] ?: run {
            MiniGameInstanceData.miniGameReJoinData[this] = ArrayList()
        }
        return MiniGameInstanceData.miniGameReJoinData[this]!!
    }

fun MiniGameInstance.clearReJoinData() {
    rejoinData.forEach {
        it.reJoinData
    }
    MiniGameInstanceData.miniGameReJoinData.remove(this)
}