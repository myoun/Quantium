package org.netherald.quantium.module.config

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class PluginDepend(vararg val values : String)
