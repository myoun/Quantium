
pluginManagement {
    val kotlinVersion: String by settings
    plugins {
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
include(":example:org.netherald.quantium.event-loagger")
findProject(":example:org.netherald.quantium.event-loagger")?.name = "org.netherald.quantium.event-logger"
include("bungee-api")
