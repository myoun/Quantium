
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
include(":bungee")
include(":bukkit")
include(":bukkit-api")
include(":common")
include(":example:duel")
include(":example:test-minigame")
include(":annotation-processor")
include(":example:event-loagger")
findProject(":example:event-loagger")?.name = "event-logger"
include("bungee-api")
