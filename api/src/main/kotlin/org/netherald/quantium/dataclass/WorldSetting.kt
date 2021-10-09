package org.netherald.quantium.dataclass

import org.bukkit.GameMode

data class WorldSetting(
    var baseWorld : String? = null,
    var baseWorldNether : String? = null,
    var baseWorldTheNether : String? = null,
    val otherWorlds : List<String> = ArrayList(),
    var linkPortal : Boolean = true,
    var enableOtherWorldTeleport : Boolean = false,
    var defaultGameMode : GameMode = GameMode.ADVENTURE
)