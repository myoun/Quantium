package org.netherald.quantium

import com.onarandombox.MultiverseCore.MultiverseCore
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.data.MiniGameData
import org.netherald.quantium.data.QuantiumConfig
import org.netherald.quantium.listener.*
import org.netherald.quantium.module.ModuleLoader
import org.netherald.quantium.util.*
import org.netherald.quantium.world.*
import java.io.File
import java.util.regex.Pattern


class QuantiumPlugin : JavaPlugin() {

    override fun onEnable() {

        saveDefaultConfig()
        Quantium.plugin = this
        Quantium.moduleLoader = ModuleLoader(this)

        server.pluginManager.registerEvents(SpawnTeleportL(), this)

        SpectatorUtil.default = QuantiumSpectatorUtil()
        PerMiniGameTabList.default = QuantiumPerMiniGameTabList()
        PerMiniGameChat.default = QuantiumPerMiniGameChat()

        server.pluginManager.registerEvents(TabListUtilL(this), this)
        server.pluginManager.registerEvents(MiniGameChatL(), this)
        server.pluginManager.registerEvents(RespawnL(), this)

        (server.pluginManager.getPlugin("Multiverse-Core") as MultiverseCore?)?.let {
            WorldEditor.default = MultiverseWorldEditor(it.mvWorldManager)
        }

        (server.pluginManager.getPlugin("Multiverse-NetherPortals") as MultiverseNetherPortals?)?.let {
            PortalLinker.default = MultiversePortalLinker(it)
        }

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
        Quantium.modules.forEach { (_, module) ->
            Quantium.moduleLoader.unloadModule(module)
        }
    }

    fun loadConfig() {

        if (config.getBoolean(ConfigPath.ENABLE_LOBBY)) QuantiumConfig.enableLobby = true
        if (config.getBoolean(ConfigPath.ENABLE_MINIGAME)) QuantiumConfig.enableMiniGame = true
        if (config.getBoolean(ConfigPath.BUNGEECORD)) QuantiumConfig.bungeecord = true

        QuantiumConfig.lobbyLocation = config.getLocation(ConfigPath.LOBBY_LOCATION)!!
    }

    fun loadModules() {

        val directory = File(dataFolder, "modules")

        if (!directory.exists()) { directory.mkdir() }
        directory.listFiles { file ->
            Pattern.matches("\\.jar$", file.name)
        }!!.forEach { file ->
            Quantium.moduleLoader.loadModule(file)
        }

        TODO()
    }
}