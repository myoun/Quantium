package org.netherald.quantium.event

import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Event
import org.netherald.quantium.MiniGameInstance

class MiniGameConnectedEvent(
    val player : ProxiedPlayer,
    val instance : MiniGameInstance,
) : Event()