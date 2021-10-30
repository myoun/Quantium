package org.netherald.quantium

import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.data.MiniGameData

fun registerMiniGame(
    plugin : JavaPlugin,
    name : String,
    minPlayerSize : Int,
    maxPlayerSize : Int,
    maxInstanceSize : Int = 1,
    defaultInstanceSize : Int = -1,
    miniGameInstanceSetting : MiniGameInstance.() -> Unit
) {
    MiniGameData.miniGames[name] = MiniGame(
        plugin,
        name,
        minPlayerSize,
        maxPlayerSize,
        maxInstanceSize,
        if (defaultInstanceSize == -1) maxInstanceSize else defaultInstanceSize,
        miniGameInstanceSetting
    ).also { it.unSafe.init() }
}

@JvmName("registerMiniGame1")
fun JavaPlugin.registerMiniGame(
    name : String,
    minPlayerSize : Int,
    maxPlayerSize : Int,
    maxInstanceSize : Int = 1,
    defaultInstanceSize : Int = -1,
    miniGameInstanceSetting : MiniGameInstance.() -> Unit
) {
    registerMiniGame(
        this,
        name,
        minPlayerSize,
        maxPlayerSize,
        maxInstanceSize,
        defaultInstanceSize,
        miniGameInstanceSetting
    )
}


fun unregisterMiniGame(name : String) {
    MiniGameData.miniGames[name]?.let {
        it.stopAll()
        MiniGameData.miniGames.remove(name)
    }
}

fun MiniGame.changeMaxInstanceSize(value : Int) {
    MiniGameData.miniGames[name]?.maxInstanceSize = value
}

fun changeMaxInstanceSize(name : String, value : Int) {
    MiniGameData.miniGames[name]?.maxInstanceSize = value
}