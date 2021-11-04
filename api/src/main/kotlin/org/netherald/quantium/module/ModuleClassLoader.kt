package org.netherald.quantium.module

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.Quantium
import org.netherald.quantium.data.ModuleData
import java.io.File
import java.net.URLClassLoader

class ModuleClassLoader(
    val plugin : JavaPlugin, file : File, parent : ClassLoader
) : URLClassLoader(arrayOf(file.toURI().toURL()), parent) {

    lateinit var module : QuantiumModule
    var config : YamlConfiguration = YamlConfiguration()

    fun loadPlugin() : QuantiumModule {
        Bukkit.getServer().pluginManager
        val configFile = getResource("quantium.yml")?.let {
            config = YamlConfiguration.loadConfiguration(it.openStream().reader())
        }
        val mainClass = loadClass(config.getString(ModuleConfigPath.MAIN))
        module = mainClass.newInstance() as QuantiumModule

        module.apply {
            name = this@ModuleClassLoader.config.getString(ModuleConfigPath.NAME)!!
            plugin = this@ModuleClassLoader.plugin
            server = Bukkit.getServer()
            dataFolder = File(plugin.dataFolder, name)
            this.configFile = File(dataFolder, ModuleConfigPath.FILE_NAME)
            config = YamlConfiguration.loadConfiguration(this.configFile)
        }

        ModuleData.modules[module.name] = module
        module.onLoad()
        return module
    }

    fun libraryLoad(path : String) {
        libraryLoader.loadClass(path)
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    private val libraryLoader : ClassLoader
        get() {
            val classLoader = Quantium.plugin.javaClass.classLoader
            return classLoader.javaClass.getDeclaredField("libraryLoader").apply {
                isAccessible = true
            }.get(classLoader) as ClassLoader
        }



    fun enablePlugin() {
        if (!module.isEnabled) {
            module.enable()
        }
    }

    fun unloadPlugin() = close()

    override fun close() {
        module.disable()
        super.close()
    }
}