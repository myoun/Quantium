package org.netherald.quantium.event

import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.plugin.Event
import org.netherald.quantium.MiniGameInfo

class InstanceAddedEvent(val server : ServerInfo, val miniGameInfo: MiniGameInfo) : Event()