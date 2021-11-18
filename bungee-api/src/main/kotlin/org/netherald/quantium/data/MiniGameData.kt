package org.netherald.quantium.data

import org.netherald.quantium.MiniGameInfo
import org.netherald.quantium.MiniGameInstance
import java.util.*
import kotlin.collections.HashMap

object MiniGameData {

    val miniGames = HashMap<String, MiniGameInfo>()
    val instances = HashMap<UUID, MiniGameInstance>()

}