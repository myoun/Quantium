package org.netherald.quantium.data

import org.netherald.quantium.MiniGameInfo
import org.netherald.quantium.MiniGameInstance
import org.netherald.quantium.RedisKeyType
import org.netherald.quantium.listener.PluginMessageL
import org.netherald.quantium.util.RedisServerUtil
import java.util.*
import kotlin.collections.HashMap

object MiniGameData {

    val miniGames = HashMap<String, MiniGameInfo>()
    val instances = HashMap<UUID, MiniGameInstance>()

    fun getMiniGameType(uuid : UUID) : MiniGameInfo? {
        return RedisServerUtil.connection?.sync()?.let {
            miniGames[it.get("${RedisKeyType.INSTANCE}:$uuid:${RedisKeyType.MINI_GAME}")]
        } ?: run {
            PluginMessageL.miniGameTypeMap[uuid]
        }
    }

}