package org.netherald.quantium.module

import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class ModuleLoader(private val plugin: JavaPlugin) {
    fun loadModule(file : File) {
        val classLoader = ModuleClassLoader(plugin, file, javaClass.classLoader)
        classLoader.loadPlugin()
    }

    fun unloadModule(module : QuantiumModule) {
        if (module.javaClass.classLoader is ModuleClassLoader) {
            (module.javaClass.classLoader as ModuleClassLoader).close()
        }
    }
}