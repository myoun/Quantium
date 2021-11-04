package org.netherald.quantium.module

import org.bukkit.Server
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.Quantium
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


abstract class QuantiumModule() {

    lateinit var name : String
    lateinit var plugin : JavaPlugin
    lateinit var server : Server
    lateinit var quantium : Quantium
    lateinit var dataFolder : File
    lateinit var configFile : File

    var isEnabled = false

    var config : FileConfiguration? = null

    fun saveDefaultConfig(replace : Boolean) {
        val save = fun () {
            (javaClass.classLoader as ModuleClassLoader).getResourceAsStream("config.yml")?.let {
                val outStream: OutputStream = FileOutputStream(configFile)
                outStream.write(it.readBytes())
                kotlin.runCatching {
                    outStream.flush()
                }
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

    fun enable() {
        if (!isEnabled) {
            onEnable()
        }
    }

    fun disable() {
        if (isEnabled) {
            onDisable()
        }
    }

    open fun onLoad() {}
    open fun onEnable() {}
    open fun onDisable() {}
}