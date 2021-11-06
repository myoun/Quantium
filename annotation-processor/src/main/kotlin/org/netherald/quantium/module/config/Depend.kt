package org.netherald.quantium.module.config

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Depend(vararg val values : String)
