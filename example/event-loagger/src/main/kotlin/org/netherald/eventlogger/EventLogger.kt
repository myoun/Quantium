package org.netherald.eventlogger

import org.netherald.quantium.Quantium
import org.netherald.quantium.module.QuantiumModule

class EventLogger : QuantiumModule() {
    override fun onEnable() {
        Quantium.moduleManager.registerEvents(EventListener(), this)
    }
}