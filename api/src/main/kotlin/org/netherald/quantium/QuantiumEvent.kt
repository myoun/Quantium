package org.netherald.quantium

import org.bukkit.event.Event

class QuantiumEvent<T : Event>(val event : T,  miniGameInstance: MiniGameInstance) : BuilderUtil(miniGameInstance)