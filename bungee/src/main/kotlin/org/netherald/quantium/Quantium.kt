package org.netherald.quantium

import io.lettuce.core.RedisURI
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import org.netherald.quantium.data.MiniGameData
import org.netherald.quantium.data.addMiniGameServer
import org.netherald.quantium.data.setLobby
import org.netherald.quantium.listener.InstanceL
import org.netherald.quantium.listener.PluginMessageL
import org.netherald.quantium.util.RedisServerUtil
import java.io.File
import java.io.IOException
import java.nio.file.Files


class Quantium : Plugin() {

    companion object {
        lateinit var config : Configuration
    }

    override fun onEnable() {

        config = configLoad()

        proxy.reconnectHandler = MiniGameReConnectHandler()
        proxy.registerChannel(Channels.MAIN_CHANNEL)
        proxy.pluginManager.registerListener(this, PluginMessageL())
        proxy.pluginManager.registerListener(this, InstanceL())

        config.getSection(ConfigPath.REDIS).apply {
            val address = getString(ConfigPath.Redis.address)!!
            val port = getInt(ConfigPath.Redis.port)
            if (getBoolean(ConfigPath.ENABLE)) {
                val url = RedisURI.create(address, port)
                getString(ConfigPath.Redis.password)?.let { password ->
                    @Suppress("DEPRECATION")
                    url.setPassword(password)
                }
                RedisServerUtil.init(url)
            }
        }

        config.getStringList(ConfigPath.LOBBY)?.forEach { serverName ->
            ProxyServer.getInstance().getServerInfo(serverName)!!.setLobby()
        }

        config.getSection(ConfigPath.MINI_GAME)?.let { miniGameSection ->
            miniGameSection.keys.forEach { name ->
                miniGameSection.getSection(name).apply {
                    val miniGame = MiniGameInfo(
                        name,
                        getInt(ConfigPath.MiniGame.MIN_PLAYER_SIZE),
                        getInt(ConfigPath.MiniGame.MAX_PLAYER_SIZE)
                    )
                    MiniGameData.miniGames[name] = miniGame
                    RedisServerUtil.addMiniGame(miniGame.name)
                    miniGameSection.getSection(ConfigPath.MiniGame.SERVERS).keys.forEach { serverName ->
                        val serverInfo = ProxyServer.getInstance().getServerInfo(serverName)!!
                        serverInfo.addMiniGameServer(miniGame)
                        getSection(serverName).apply {
                            (miniGame.maxInstanceCount as MutableMap<ServerInfo, Int>)[serverInfo] =
                                getInt(ConfigPath.MiniGame.MAX_INSTANCE_SIZE)
                        }
                    }
                }
            }
        }
    }

    override fun onDisable() {
        RedisServerUtil.client?.shutdown()
    }

    fun configLoad() : Configuration {
        saveDefaultConfig()
        return loadData()
    }

    fun saveDefaultConfig() {
        if (!dataFolder.exists()) dataFolder.mkdir()
        val file = File(dataFolder, "config.yml")
        if (!file.exists()) {
            try {
                getResourceAsStream("config.yml").use { `in` -> Files.copy(`in`, file.toPath()) }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun loadData() : Configuration {
        return ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(
            File(
                dataFolder, "config.yml"
            )
        )
    }

    fun saveConfig() {
        ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(
            config, File(
                dataFolder, "config.yml"
            )
        )
    }

}