package org.netherald.quantium.world

import org.netherald.quantium.MiniGameInstance

interface PerMiniGameChat {
    companion object {
        lateinit var default : PerMiniGameChat
    }
    // String is worldName
    fun applyPerChat(minigame : MiniGameInstance)

    fun unApplyPerChat(minigame : MiniGameInstance)

}