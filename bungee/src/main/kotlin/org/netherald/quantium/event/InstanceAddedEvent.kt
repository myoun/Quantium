package org.netherald.quantium.event

import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.plugin.Event
import org.netherald.quantium.MiniGameInfo
import org.netherald.quantium.MiniGameInstance
import org.netherald.quantium.data.MiniGameData
import java.util.*

class InstanceAddedEvent(val instance : MiniGameInstance) : Event() {
    val miniGame : MiniGameInfo get() = instance.miniGame
}