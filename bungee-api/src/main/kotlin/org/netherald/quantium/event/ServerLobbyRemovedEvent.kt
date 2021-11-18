package org.netherald.quantium.event

import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.plugin.Event

class ServerLobbyRemovedEvent(val serverInfo : ServerInfo) : Event()