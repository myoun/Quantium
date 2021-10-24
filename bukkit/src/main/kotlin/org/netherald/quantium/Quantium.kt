package org.netherald.quantium

import com.onarandombox.MultiverseCore.MultiverseCore
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.data.MiniGameData
import org.netherald.quantium.data.QuantiumConfig
import org.netherald.quantium.listener.*
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
        server.pluginManager.registerEvents(RespawnL(), this)

        loadConfig()

        if (QuantiumConfig.bungeecord) {
            PlayerUtil.default = PluginMessagePlayerUtil()
            server.messenger.registerIncomingPluginChannel(this, Channels.mainChannel, PluginMessageL())
        } else {
            PlayerUtil.default = QuantiumPlayerUtil()
        }

    }

    override fun onDisable() {
        MiniGameData.miniGames.forEach { (_, minigame) ->
            minigame.defaultInstanceSize = 0
            val iterator = minigame.instances.iterator() as MutableIterator
            while (iterator.hasNext()) {
                val instance = iterator.next()
                iterator.remove()
                instance.delete()
            }
        }
    }

    fun loadConfig() {

        if (config.getBoolean(ConfigPath.ENABLE_LOBBY)) QuantiumConfig.enableLobby = true
        if (config.getBoolean(ConfigPath.ENABLE_MINIGAME)) QuantiumConfig.enableMiniGame = true
        if (config.getBoolean(ConfigPath.BUNGEECORD)) QuantiumConfig.bungeecord = true

        QuantiumConfig.lobbyLocation = config.getLocation(ConfigPath.LOBBY_LOCATION)!!
    }
}