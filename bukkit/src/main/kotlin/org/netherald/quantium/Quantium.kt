package org.netherald.quantium

import com.onarandombox.MultiverseCore.MultiverseCore
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.data.QuantiumConfig
import org.netherald.quantium.listener.SpawnTeleportL
import org.netherald.quantium.listener.MiniGameChatL
import org.netherald.quantium.listener.PluginMessageL
import org.netherald.quantium.listener.TabListUtilL
import org.netherald.quantium.util.*
import org.netherald.quantium.world.*


class Quantium : JavaPlugin() {

    override fun onEnable() {

        saveDefaultConfig()

        server.pluginManager.registerEvents(SpawnTeleportL(), this)

        (server.pluginManager.getPlugin("Multiverse-Core") as MultiverseCore?)?.let {
            WorldEditor.default = MultiverseWorldEditor(it.mvWorldManager)
        }

        (server.pluginManager.getPlugin("Multiverse-NetherPortals") as MultiverseNetherPortals?)?.let {
            PortalLinker.default = MultiversePortalLinker(it)
        }

        SpectatorUtil.default = QuantiumSpectatorUtil()
        PerMiniGameTabList.default = QuantiumPerMiniGameTabList()
        PerMiniGameChat.default = QuantiumPerMiniGameChat()

        server.pluginManager.registerEvents(TabListUtilL(this), this)
        server.pluginManager.registerEvents(MiniGameChatL(), this)

        if (config.getBoolean(ConfigPath.ENABLE_LOBBY)) QuantiumConfig.enableLobby = true
        if (config.getBoolean(ConfigPath.ENABLE_MINIGAME)) QuantiumConfig.enableMiniGame = true
        if (config.getBoolean(ConfigPath.BUNGEECORD)) {
            QuantiumConfig.bungeecord = true
            PlayerUtil.default = PluginMessagePlayerUtil()
            server.messenger.registerIncomingPluginChannel(this, Channels.mainChannel, PluginMessageL())
        } else {
            PlayerUtil.default = QuantiumPlayerUtil()
        }

    }

    override fun onDisable() {
    }
}