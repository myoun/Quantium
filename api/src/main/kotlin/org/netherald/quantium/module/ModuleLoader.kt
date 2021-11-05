package org.netherald.quantium.module

import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.data.ModuleData
import java.io.File

class ModuleLoader(private val plugin: JavaPlugin) {

    fun getModule(name : String) = ModuleData.modules[name]

    fun loadModule(file : File) : QuantiumModule {
        val classLoader = ModuleClassLoader(plugin, file, javaClass.classLoader)
        return classLoader.loadModule()
    }

    fun enableModule(module : QuantiumModule) : Boolean {
        module.classLoader.enableModule()
        return module.isEnabled
    }

    fun unloadModule(module : QuantiumModule) {
        module.classLoader.close()
    }

}