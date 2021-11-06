package org.netherald.quantium.module.config

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Libraries(vararg val values : String)
