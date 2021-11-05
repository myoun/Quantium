@file:Suppress("UNCHECKED_CAST")

package org.netherald.quantium.module

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.PluginLoader
import org.bukkit.plugin.SimplePluginManager
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import org.netherald.quantium.Quantium
import org.netherald.quantium.data.ModuleData
import java.io.File
import java.net.URLClassLoader
import java.util.regex.Pattern

class ModuleClassLoader(
    val plugin : JavaPlugin, file : File, parent : ClassLoader
) : URLClassLoader(arrayOf(file.toURI().toURL()), parent) {

    lateinit var module : QuantiumModule
    lateinit var config : YamlConfiguration
    lateinit var libraryLoader : ClassLoader

    private var loaded = false
    private var enabled = false

    val isLoaded : Boolean get() = loaded
    val isEnabled : Boolean get() = enabled

    val moduleName : String get() = config.getString(ModuleConfigPath.NAME)!!

    init {
        getResource(ModuleConfigPath.FILE_NAME)?.let {
            config = YamlConfiguration.loadConfiguration(it.openStream().reader())
        } ?: run {
            throw ModuleLoadException("Can't found ${ModuleConfigPath.FILE_NAME}!")
        }
    }

    fun loadModule() : QuantiumModule {

        if (isLoaded) return module

        val mainClass = loadClass(config.getString(ModuleConfigPath.MAIN))
        module = mainClass.newInstance() as QuantiumModule
        libraryLoader = createLoader()
        module.patchData()

        ModuleData.modules[module.name] = module
        module.onLoad()
        loaded = true

        return module

    }

    private fun createLoader() : ClassLoader {
        val descriptionFile = PluginDescriptionFile(moduleName, "", "")
        descriptionFile.libraries.addAll(config.getStringList(ModuleConfigPath.LIBRARIES))
        fileAssociations.forEach { (pattern, it) ->
            if (pattern.matcher("test.jar").find()) {
                if (it is JavaPluginLoader) {
                    val libraryLoader =
                        JavaPluginLoader::class.java.getDeclaredField("libraryLoader").apply {
                            isAccessible = true
                        }[it]
                    return libraryLoader.javaClass
                        .getDeclaredMethod("createLoader").apply {
                            isAccessible = true
                        }.invoke(libraryLoader, descriptionFile) as ClassLoader

                } else throw Exception("Wrong pluginLoader")
            }
        }
        throw Exception("not found pattern")
    }

    private val fileAssociations : Map<Pattern, PluginLoader>
        get() = SimplePluginManager::class.java
            .getDeclaredField("fileAssociations").apply {
                isAccessible = true
            }[Bukkit.getPluginManager()] as Map<Pattern, PluginLoader>

    fun enableModule() {
        if (!::module.isInitialized) throw RuntimeException("$moduleName is not loaded!")
        if (module.isEnabled) return

        config.getStringList(ModuleConfigPath.DEPEND).forEach { name ->
            Quantium.modules[name]?.classLoader?.enableModule() ?: run {
                throw ModuleLoadException("$moduleName Not found depend $name")
            }
        }
        config.getStringList(ModuleConfigPath.PLUGIN_DEPEND).forEach { name ->
            Bukkit.getPluginManager().getPlugin(name)?.let {
                it.pluginLoader.enablePlugin(it)
                if (!it.isEnabled) {
                    throw ModuleEnableException("$moduleName can't enable plugin $name")
                }
            } ?: run {
                throw ModuleEnableException("$moduleName Not found plugin-depend $name")
            }
        }
        config.getStringList(ModuleConfigPath.SOFT_DEPEND).forEach { name ->
            Quantium.modules[name]?.classLoader?.enableModule()
        }
        config.getStringList(ModuleConfigPath.PLUGIN_SOFT_DEPEND).forEach { name ->
            Bukkit.getPluginManager().getPlugin(name)?.let {
                it.pluginLoader.enablePlugin(it)
            }
        }
        module.enable()
    }

    private fun QuantiumModule.enable() {
        if (!isEnabled) {
            module.onEnable()
        }
        enabled = true
    }

    private fun QuantiumModule.disable() {
        if (isEnabled) {
            module.onDisable()
        }
        enabled = false
    }

    private fun QuantiumModule.patchData() {
        name = this@ModuleClassLoader.config.getString(ModuleConfigPath.NAME)!!
        plugin = this@ModuleClassLoader.plugin
        server = Bukkit.getServer()
        dataFolder = File(plugin.dataFolder, name)
        this.configFile = File(dataFolder, ModuleConfigPath.FILE_NAME)
        config = YamlConfiguration.loadConfiguration(this.configFile)
        libraryLoader = this@ModuleClassLoader.libraryLoader
    }

    fun unloadModule() = close()

    override fun close() {
        val isDependsThis =
            fun QuantiumModule.() =
                classLoader.config.getStringList(ModuleConfigPath.DEPEND).contains(module.name) ||
                        classLoader.config.getStringList(ModuleConfigPath.SOFT_DEPEND).contains(module.name)

        Quantium.modules.forEach { (_, module) ->
            if (!module.isEnabled) return@forEach
            if (module.isDependsThis()) {
                module.classLoader.close()
            }
        }
        module.disable()
        super.close()
    }
}