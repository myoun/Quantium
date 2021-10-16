package org.netherald.quantium.world

import org.netherald.quantium.MiniGameInstance

interface PerMiniGameTabList {
    companion object {
        lateinit var default : PerMiniGameTabList
    }
    // String is worldName
    fun applyPerTabList(minigame : MiniGameInstance)

    fun unApplyPerTabList(minigame : MiniGameInstance)

}