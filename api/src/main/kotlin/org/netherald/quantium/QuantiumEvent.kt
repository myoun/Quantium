package org.netherald.quantium

import org.bukkit.event.Event
import org.netherald.quantium.util.MiniGameBuilderUtil

class QuantiumEvent<T : Event>(val event : T,  miniGameInstance: MiniGameInstance) : MiniGameBuilderUtil(miniGameInstance)