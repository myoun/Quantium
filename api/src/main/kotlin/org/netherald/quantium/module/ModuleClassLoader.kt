@file:Suppress("UNCHECKED_CAST")

package org.netherald.quantium.module

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
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
    private lateinit var config : YamlConfiguration
    lateinit var libraryLoader : ClassLoader

    private var loaded : Boolean = false
    val isLoaded : Boolean get() = loaded
    val isEnabled : Boolean get() = module.isEnabled

    val moduleName : String get() = config.getString(ModuleConfigPath.NAME)!!

    init {
        getResource(ModuleConfigPath.FILE_NAME+"yml")?.let {
            config = YamlConfiguration.loadConfiguration(it.openStream().reader())
        } ?: run {
            throw ModuleLoadException("Can't found ${ModuleConfigPath.FILE_NAME+"yml"}!")
        }
    }

    val depend : Collection<QuantiumModule> get() = config.getStringList(ModuleConfigPath.DEPEND).map {
        Quantium.modules[it]!!
    }

    val softDepend : Collection<QuantiumModule> get() = config.getStringList(ModuleConfigPath.SOFT_DEPEND).map {
        Quantium.modules[it]!!
    }

    val pluginDepend : Collection<Plugin> get() = config.getStringList(ModuleConfigPath.PLUGIN_DEPEND).map {
        Bukkit.getPluginManager().getPlugin(it)!!
    }

    val pluginSoftDepend : Collection<Plugin> get() = config.getStringList(ModuleConfigPath.PLUGIN_SOFT_DEPEND).map {
        Bukkit.getPluginManager().getPlugin(it)!!
    }

    fun loadModule() : QuantiumModule {

        if (isLoaded) return module

        libraryLoader = createLoader()
        val modulePath = config.getString(ModuleConfigPath.MAIN)
        val moduleName = config.getString(ModuleConfigPath.MAIN)
        moduleName ?: throw ModuleLoadException("Not found Name $modulePath")
        modulePath ?: throw ModuleLoadException("Not found Main $moduleName")
        val mainClass = loadClass(config.getString(ModuleConfigPath.MAIN))

        if (!QuantiumModule::class.java.isAssignableFrom(mainClass)) {
            throw ModuleLoadException("${mainClass.simpleName} is not QuantiumModule")
        }
        module = mainClass.newInstance() as QuantiumModule
        module.patchData()

        ModuleData.modules[module.name] = module
        module.onLoad()
        plugin.logger.info("$moduleName is loaded")
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
        throw Exception("Not found pattern")
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
        module.setEnabled(true)
    }

    private fun QuantiumModule.patchData() {
        name = moduleName
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
            fun QuantiumModule.() = classLoader.depend.contains(module) || classLoader.softDepend.contains(module)

        Quantium.modules.forEach { (_, module) ->
            if (!module.isEnabled) return@forEach
            if (module.isDependsThis()) {
                module.classLoader.close()
            }
        }
        val moduleName = moduleName
        module.setEnabled(false)
        super.close()
        plugin.logger.info("Module $moduleName is unloaded")
    }
}