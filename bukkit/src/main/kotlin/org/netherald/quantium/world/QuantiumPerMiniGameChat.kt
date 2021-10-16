package org.netherald.quantium.world

import org.netherald.quantium.MiniGameInstance
import org.netherald.quantium.listener.MiniGameChatL

class QuantiumPerMiniGameChat : PerMiniGameChat {
    override fun applyPerChat(minigame: MiniGameInstance) {
        MiniGameChatL.targetMiniGame.add(minigame)
    }

    override fun unApplyPerChat(minigame: MiniGameInstance) {
        MiniGameChatL.targetMiniGame.remove(minigame)
    }
}