package org.netherald.quantium

import com.onarandombox.MultiverseCore.MultiverseCore
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.dataclass.MiniGameData
import org.netherald.quantium.listener.ConnectedListener
import org.netherald.quantium.listener.PluginMessageL
import org.netherald.quantium.util.QuantiumPlayerUtil
import org.netherald.quantium.util.PluginMessagePlayerUtil
import org.netherald.quantium.world.MultiverseWorldEditor
import org.netherald.quantium.world.MultiverseWorldLinker
import org.netherald.quantium.world.WorldEditor
import org.netherald.quantium.world.WorldLinker


class Quantium : JavaPlugin() {

    override fun onEnable() {

        saveDefaultConfig()

        QuantiumBuilder.plugin = this

        (server.pluginManager.getPlugin("Multiverse-Core") as MultiverseCore?)?.let {
            WorldEditor.worldEditor = MultiverseWorldEditor(it.mvWorldManager)
        }

        (server.pluginManager.getPlugin("Multiverse-NetherPortals") as MultiverseNetherPortals?)?.let {
            WorldLinker.worldLinker = MultiverseWorldLinker(it)
        }

        server.pluginManager.registerEvents(ConnectedListener(), this)
        if (config.getBoolean("bungeecord")) {
            QuantiumPlayerUtil.playerUtil = PluginMessagePlayerUtil()
            server.messenger.registerIncomingPluginChannel(this, Channels.mainChannel, PluginMessageL())
        } else {

        }


    }

    override fun onDisable() {
        MiniGameData.miniGames.forEach {
            it.stopAll()
        }
    }
}