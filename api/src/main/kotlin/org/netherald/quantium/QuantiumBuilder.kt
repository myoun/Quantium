package org.netherald.quantium

import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.QuantiumBuilder.miniGameData
import org.netherald.quantium.dataclass.TeamSetting
import org.netherald.quantium.dataclass.WorldSetting
import kotlin.collections.HashMap

@QuantiumMarker
object QuantiumBuilder {
    lateinit var plugin: JavaPlugin
    val miniGameData = HashMap<String, MiniGame>()
}

@JvmName("registerMiniGame1")
fun JavaPlugin.registerMiniGame(
    name : String,
    minPlayerSize : Int,
    maxPlayerSize : Int,
    enableRejoin : Boolean = false,
    perPlayerList: Boolean = true,
    maxInstanceSize : Int = 1,
    defaultInstanceSize : Int = -1,
    teamSetting : TeamSetting.() -> Unit = {},
    worldSetting: WorldSetting.() -> Unit = {},
    miniGameInstanceSetting : MiniGameInstance.() -> Unit
) {
    registerMiniGame(
        this,
        name,
        minPlayerSize,
        maxPlayerSize,
        enableRejoin,
        maxInstanceSize,
        defaultInstanceSize,
        perPlayerList,
        teamSetting,
        worldSetting,
        miniGameInstanceSetting
    )
}

fun registerMiniGame(
    plugin : JavaPlugin,
    name : String,
    minPlayerSize : Int,
    maxPlayerSize : Int,
    enableRejoin : Boolean = false,
    maxInstanceSize : Int = 1,
    defaultInstanceSize : Int = -1,
    perPlayerList : Boolean = true,
    teamSetting : TeamSetting.() -> Unit = {},
    worldSetting: WorldSetting.() -> Unit = {},
    miniGameInstanceSetting : MiniGameInstance.() -> Unit
) {
    val teamSettingValue = TeamSetting().apply(teamSetting)
    val worldSettingValue = WorldSetting().apply(worldSetting)
    miniGameData[name] = MiniGame(
        plugin,
        name,
        minPlayerSize,
        maxPlayerSize,
        enableRejoin,
        perPlayerList,
        maxInstanceSize,
        if (defaultInstanceSize == -1) maxInstanceSize else defaultInstanceSize,
        teamSettingValue,
        worldSettingValue,
        miniGameInstanceSetting
    )
}

fun unregisterMiniGame(name : String) {
    miniGameData[name]?.let {
        it.stopAll()
        miniGameData.remove(name)
    }
}

fun MiniGame.changeMaxInstanceSize(value : Int) {
    miniGameData[name]?.maxInstanceSize = value
}

fun changeMaxInstanceSize(name : String, value : Int) {
    miniGameData[name]?.maxInstanceSize = value
}