package org.netherald.quantium

import net.md_5.bungee.api.plugin.Plugin
import org.netherald.quantium.data.QuantiumConfig

fun debug(message : String) {
    if (QuantiumConfig.isDebug) {
        Quantium.plugin.logger.info("Debug: $message")
    }
}

object Quantium {
    lateinit var plugin : Plugin
}