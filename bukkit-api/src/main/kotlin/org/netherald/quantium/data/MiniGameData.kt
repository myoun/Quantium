package org.netherald.quantium.data

import org.netherald.quantium.MiniGame
import org.netherald.quantium.MiniGameInstance
import java.util.*
import kotlin.collections.HashMap

object MiniGameData {
    val miniGames = HashMap<String, MiniGame>()
    val instances = HashMap<UUID, MiniGameInstance>()
}