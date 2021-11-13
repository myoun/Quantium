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
                QuantiumConfig.Redis.password?.let { password ->
                    RedisServerUtil.instance = RedisServerUtil(
                        serverName,
                        RedisURI.create(QuantiumConfig.Redis.address, QuantiumConfig.Redis.port).apply {
                            @Suppress("DEPRECATION")
                            setPassword(password)
                        }
                    )
                } ?: run {
                    RedisServerUtil.instance = RedisServerUtil(
                        serverName,
                        RedisURI.create(QuantiumConfig.Redis.address, QuantiumConfig.Redis.port)
                    )
                }
                ServerUtil.default = RedisServerUtil.instance!!
            } else {
                ServerUtil.default = PluginMessageServerUtil(serverName)
            }
            PlayerUtil.default = PluginMessagePlayerUtil()
            server.messenger.registerIncomingPluginChannel(this, Channels.MAIN_CHANNEL, PluginMessageL())
        } else {
            PlayerUtil.default = QuantiumPlayerUtil()
        }

        val command = QuantiumCommand()
        getCommand("quantiumbukkit")!!.setExecutor(command)
        getCommand("quantiumbukkit")!!.tabCompleter = command

        loadModules()
    }

    override fun onDisable() {
        MiniGameData.miniGames.forEach { (_, miniGame) ->
            miniGame.defaultInstanceSize = 0
            val iterator = miniGame.instances.iterator() as MutableIterator
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

        val throwException = fun (type : String): Nothing = throw Exception("Not found $type")

        QuantiumConfig.lobbyLocation = config.getLocation(ConfigPath.LOBBY_LOCATION)
            ?: throwException(ConfigPath.LOBBY_LOCATION)

        config.getConfigurationSection(ConfigPath.BUNGEECORD)?.apply {

            if (getBoolean(ConfigPath.ENABLE)) QuantiumConfig.Bungee.enable = true

            QuantiumConfig.Bungee.serverName = getString(ConfigPath.Bungee.SERVER_NAME)
                ?: throwException(ConfigPath.Bungee.SERVER_NAME)

        }

        config.getConfigurationSection(ConfigPath.REDIS)?.apply {

            QuantiumConfig.Redis.enable = getBoolean(ConfigPath.ENABLE)

            QuantiumConfig.Redis.address = getString(ConfigPath.Redis.ADDRESS)
                ?: throwException(ConfigPath.Redis.ADDRESS)

            QuantiumConfig.Redis.port = getInt(ConfigPath.Redis.PORT)
            if (QuantiumConfig.Redis.port == 0) throwException(ConfigPath.Redis.PORT)

            QuantiumConfig.Redis.password = getString(ConfigPath.Redis.PASSWORD)
                ?: throwException(ConfigPath.Redis.PASSWORD)

        }
    }

    fun loadModules() {

        val directory = File(dataFolder, "modules")

        if (!directory.exists()) { directory.mkdir() }
        directory.listFiles { file ->
            !file.isDirectory && Pattern.compile("\\.jar$").matcher(file.name).find()
        }!!.forEach { file ->
            kotlin.runCatching { Quantium.moduleLoader.loadModule(file) }.exceptionOrNull()?.printStackTrace()
        }

        Quantium.modules.forEach { (_, module) ->
            kotlin.runCatching { module.classLoader.enableModule() }.exceptionOrNull()?.printStackTrace()
        }
    }
}