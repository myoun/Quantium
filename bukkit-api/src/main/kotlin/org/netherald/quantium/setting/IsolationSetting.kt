package org.netherald.quantium.setting

import org.netherald.quantium.world.PerMiniGameChat
import org.netherald.quantium.world.PerMiniGameTabList

data class IsolationSetting(
    var perChat : Boolean = true,
    var perPlayerList : Boolean = true,
) {
    var perMiniGameChatUtil : PerMiniGameChat = PerMiniGameChat.default
    var perMiniGameTabListUtil : PerMiniGameTabList = PerMiniGameTabList.default
}