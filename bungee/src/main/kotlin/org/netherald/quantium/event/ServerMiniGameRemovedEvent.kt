package org.netherald.quantium.event

import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.plugin.Event
import org.netherald.quantium.MiniGameInfo

class ServerMiniGameRemovedEvent(
    serverInfo: ServerInfo,
    miniGameInfo: MiniGameInfo
) : Event()