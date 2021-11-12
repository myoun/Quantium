package org.netherald.quantium

import com.onarandombox.MultiverseCore.MultiverseCore
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals
import io.lettuce.core.RedisURI
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

        server.pluginManager.registerEvents(PluginDisableL(), this)
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

        if (QuantiumConfig.Bungee.enable) {
            val serverName = QuantiumConfig.Bungee.serverName
            if (QuantiumConfig.Redis.enable) {
                RedisServerUtil(serverName, RedisURI.create(QuantiumConfig.Redis.address, QuantiumConfig.Redis.port))
            } else {
                PluginMessageServerUtil(serverName)
            }
            PlayerUtil.default = PluginMessagePlayerUtil()
            server.messenger.registerIncomingPluginChannel(this, Channels.mainChannel, PluginMessageL())
        } else {
            PlayerUtil.default = QuantiumPlayerUtil()
        }

        val command = QuantiumCommand()
        getCommand("quantiumbukkit")!!.setExecutor(command)
        getCommand("quantiumbukkit")!!.tabCompleter = command

        loadModules()
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

    private fun loadConfig() {

        if (config.getBoolean(ConfigPath.ENABLE_LOBBY)) QuantiumConfig.enableLobby = true
        if (config.getBoolean(ConfigPath.ENABLE_MINIGAME)) QuantiumConfig.enableMiniGame = true

        QuantiumConfig.lobbyLocation = config.getLocation(ConfigPath.LOBBY_LOCATION)!!

        config.getConfigurationSection(ConfigPath.BUNGEECORD)?.apply {
            if (config.getBoolean(ConfigPath.ENABLE)) QuantiumConfig.Bungee.enable = true
            QuantiumConfig.Bungee.serverName = config.getString(ConfigPath.Bungee.SERVER_NAME)!!
        }

        config.getConfigurationSection(ConfigPath.REDIS)?.apply {
            QuantiumConfig.Redis.enable = getBoolean(ConfigPath.ENABLE)
            QuantiumConfig.Redis.address = getString(ConfigPath.Redis.ADDRESS)!!
            QuantiumConfig.Redis.port = getInt(ConfigPath.Redis.PORT)
            QuantiumConfig.Redis.password = getString(ConfigPath.Redis.PASSWORD)!!
        }
    }

    fun loadModules() {

        val directory = File(dataFolder, "modules")

        if (!directory.exists()) { directory.mkdir() }
        directory.listFiles { file ->
            if (!file.isDirectory) Pattern.matches("\\.jar$", file.name) else false
        }!!.forEach { file ->
            Quantium.moduleLoader.loadModule(file)
        }

        Quantium.modules.forEach { (_, module) ->
            module.classLoader.enableModule()
        }
    }
}