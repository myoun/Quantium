package org.netherald.quantium.module

import org.netherald.quantium.module.config.Depend
import org.netherald.quantium.module.config.Libraries
import org.netherald.quantium.module.config.PluginSoftDepend
import org.netherald.quantium.module.config.SoftDepend

object ModuleAnnotationMap {

    operator fun get(name : String) = map[name]

    private val map = HashMap<String, String>().apply {
        put(Depend::class.java.simpleName, ModuleConfigPath.DEPEND)
        put(SoftDepend::class.java.simpleName, ModuleConfigPath.SOFT_DEPEND)
        put(PluginSoftDepend::class.java.simpleName, ModuleConfigPath.PLUGIN_DEPEND)
        put(PluginSoftDepend::class.java.simpleName, ModuleConfigPath.PLUGIN_SOFT_DEPEND)
        put(Libraries::class.java.simpleName, ModuleConfigPath.LIBRARIES)
    }
}