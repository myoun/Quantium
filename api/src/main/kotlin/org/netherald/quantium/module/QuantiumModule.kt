package org.netherald.quantium.module

import com.google.common.base.Charsets
import org.bukkit.Server
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.Quantium
import java.io.*


abstract class QuantiumModule {

    lateinit var name : String
    lateinit var plugin : JavaPlugin
    lateinit var server : Server
    lateinit var quantium : Quantium
    lateinit var dataFolder : File
    lateinit var configFile : File
    lateinit var libraryLoader : ClassLoader

    private var enabled : Boolean = false
    val isEnabled : Boolean get() = enabled

    fun setEnabled(enabled : Boolean) {
        if (enabled != isEnabled) {
            if (enabled) {
                onEnable()
                this.enabled = true
                plugin.logger.info("Module $name is enabled")
            } else {
                this.enabled = false
                Quantium.moduleManager.unregisterEvents(this)
                tasks.forEach { it.cancel() }
                val isDependsThis =
                    fun QuantiumModule.() = classLoader.depend.contains(this) || classLoader.softDepend.contains(this)
                Quantium.modules.forEach { (_, module) ->
                    if (!module.isEnabled) return@forEach
                    if (module.isDependsThis()) {
                        module.classLoader.close()
                    }
                }
                onDisable()
                plugin.logger.info("Module $name is disabled")
            }
        }
    }

    var config : FileConfiguration = YamlConfiguration()

    fun saveDefaultConfig(replace : Boolean = false) {
        saveResource("config.yml", replace)
    }

    fun reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile)

        val defConfigStream = getResource("config.yml")

        config.setDefaults(YamlConfiguration.loadConfiguration(InputStreamReader(defConfigStream, Charsets.UTF_8)))
    }

    fun saveResource(resourcePath : String, replace : Boolean) {
        val save = fun () {
            getResource(resourcePath).let {
                val outStream: OutputStream = FileOutputStream(File(dataFolder, resourcePath))
                outStream.write(it.readBytes())
                runCatching { outStream.flush() }
                outStream.close()
            }
        }

        if (replace) {
            save()
        } else {
            if (!configFile.exists()) {
                save()
            }
        }
    }

    fun getResource(resourcePath : String) : InputStream {
        return classLoader.getResourceAsStream(resourcePath) ?: throw Exception("Null resource")
    }

    fun saveConfig() = config.save(configFile)

    val tasks : List<QuantiumTask>
        get() {
            Quantium.scheduler.taskMap[this] ?: run { Quantium.scheduler.taskMap[this] = ArrayList() }
            return Quantium.scheduler.taskMap[this]!!
        }

    open fun onLoad() {}
    open fun onEnable() {}
    open fun onDisable() {}

    val classLoader : ModuleClassLoader get() = javaClass.classLoader as ModuleClassLoader
}