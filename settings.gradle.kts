
pluginManagement {
    val kotlinVersion: String by settings
    val kspVersion: String by settings
    plugins {
        id("com.google.devtools.ksp") version kspVersion
        kotlin("jvm") version kotlinVersion
    }
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "Quantium"
include("bungee")
include("bukkit")
include("api")
include("common")
include(":example:duel")
include(":example:uhc")
include(":example:hrspectator")
findProject(":example:hrspectator")?.name = "hrspectator"
include("annotation-processor")
