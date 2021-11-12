package org.netherald.quantium.event

import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Event
import org.netherald.quantium.MiniGameInfo

class MiniGameConnectedEvent(
    val player : ProxiedPlayer,
    val minigame : MiniGameInfo,
) : Event()