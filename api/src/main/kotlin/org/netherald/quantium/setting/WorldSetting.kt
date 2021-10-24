package org.netherald.quantium.setting

import org.bukkit.Location
import org.bukkit.World
import org.netherald.quantium.world.PortalLinker
import org.netherald.quantium.world.WorldEditor

data class WorldSetting(
    var baseWorld : World? = null,
    var baseWorldNether : World? = null,
    var baseWorldTheNether : World? = null,
    val otherBaseWorlds : List<World> = ArrayList(),
    var linkPortal : Boolean = true,
    var enableOtherWorldTeleport : Boolean = false,
    var portalLinker : PortalLinker = PortalLinker.default,
    var worldEditor: WorldEditor = WorldEditor.default,
    var spawn : Location? = null,
)