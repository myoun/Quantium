package org.netherald.quantium

import io.lettuce.core.RedisURI
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import org.netherald.quantium.data.MiniGameData
import org.netherald.quantium.data.QuantiumConfig
import org.netherald.quantium.data.addMiniGameServer
import org.netherald.quantium.data.setLobby
import org.netherald.quantium.listener.ConnectedL
import org.netherald.quantium.listener.InstanceL
import org.netherald.quantium.listener.InstancePublishL
import org.netherald.quantium.listener.PluginMessageL
import org.netherald.quantium.util.RedisServerUtil
import java.io.File
import java.io.IOException
import java.lang.NullPointerException
import java.nio.file.Files
import java.util.*


class Quantium : Plugin() {

    companion object {
        lateinit var instance : Quantium
        lateinit var config : Configuration
    }

    override fun onEnable() {

        instance = this

        config = configLoad()

        proxy.reconnectHandler = MiniGameReConnectHandler()
        proxy.registerChannel(Channels.MAIN_CHANNEL)
        proxy.pluginManager.registerListener(this, PluginMessageL())
        proxy.pluginManager.registerListener(this, InstanceL())
        proxy.pluginManager.registerListener(this, ConnectedL())


        config.getSection(ConfigPath.REDIS).apply {
            val address = getString(ConfigPath.Redis.address)!!
            val port = getInt(ConfigPath.Redis.port)
            if (getBoolean(ConfigPath.ENABLE)) {
                QuantiumConfig.isRedis = true
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

        if (config.getString(ConfigPath.QUEUE_SERVER).isEmpty()) {
            throw NullPointerException("Not found queue server data in config")
        } else {
            QuantiumConfig.queueServer = proxy.getServerInfo(config.getString(ConfigPath.QUEUE_SERVER))
                ?: throw NullPointerException("Not found queue server data in bungee")
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
                    logger.info("Mini-game ${miniGame.name} is added")
                    miniGameSection.getSection(ConfigPath.MiniGame.SERVERS).keys.forEach { serverName ->
                        val serverInfo = ProxyServer.getInstance().getServerInfo(serverName)!!
                        serverInfo.addMiniGameServer(
                            miniGame,
                            getInt("$serverName.${ConfigPath.MiniGame.MAX_INSTANCE_SIZE}")
                        )
                        logger.info("Server $serverName loaded $miniGame")
                    }
                }
            }
        }

        if (QuantiumConfig.isRedis) {
            MiniGameData.miniGames.forEach { name, miniGame ->

                val instances = RedisServerUtil.connection!!.sync().smembers(
                    "${RedisKeyType.MINI_GAME}:${name}:${RedisKeyType.INSTANCES}"
                )

                instances.forEach { uuid ->
                    val server = proxy.getServerInfo(
                        RedisServerUtil.sync!!.get(
                            "${RedisKeyType.INSTANCE}:$uuid:${RedisKeyType.SERVER}"
                        )
                    ) ?: run {
                        throw NullPointerException("Not found server of $uuid. $uuid is $name mini-game")
                    }

                    val instance = MiniGameInstance(UUID.fromString(uuid), server, miniGame)
                    val connection = RedisServerUtil.client!!.connectPubSub()
                    connection.addListener(InstancePublishL(instance))

                    connection.sync().subscribe(
                        "${RedisKeyType.INSTANCE}:$uuid:${RedisKeyType.INSTANCE_STARTED}"
                    )
                    connection.sync().subscribe(
                        "${RedisKeyType.INSTANCE}:$uuid:${RedisKeyType.INSTANCE_STOPPED}"
                    )
                    RedisServerUtil.instanceConnection[instance] = connection
                    logger.info("Instance $uuid of $name is loaded")
                }
            }
        }
    }

    override fun onDisable() {
        RedisServerUtil.client?.shutdown()
    }

    fun loadMiniGamesByRedis() {
        RedisServerUtil
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
