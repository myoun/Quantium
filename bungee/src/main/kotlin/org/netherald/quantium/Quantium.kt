package org.netherald.quantium

import io.lettuce.core.RedisURI
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import org.netherald.quantium.data.addMiniGame
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
        proxy.registerChannel(Channels.mainChannel)
        proxy.pluginManager.registerListener(this, PluginMessageL())
        proxy.pluginManager.registerListener(this, InstanceL())

        config.getSection(ConfigPath.lobby)?.let {
            it.keys.forEach { serverName ->
                ProxyServer.getInstance().getServerInfo(serverName).setLobby()
            }
        }

        config.getSection(ConfigPath.minigame)?.let { miniGameSection ->
            miniGameSection.keys.forEach { miniGame ->
                miniGameSection.getSection(miniGame).keys.forEach { serverName ->
                    ProxyServer.getInstance().getServerInfo(serverName)?.addMiniGame(miniGame)
                }
            }
        }
        config.getSection(ConfigPath.redis).apply {
            val address = getString(ConfigPath.Redis.address)!!
            val port = getInt(ConfigPath.Redis.port)
            RedisServerUtil.init(
                RedisURI.create(address, port)
            )
        }
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