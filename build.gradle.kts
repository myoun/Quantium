plugins {
    kotlin("jvm") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "org.netherald"
version = "1.0-SNAPSHOT"

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "com.github.johnrengelman.shadow")

    if (name == "bukkit" || name == "api") {
        repositories {
            maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        }

        dependencies {
            compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
        }
    }
}

allprojects {
    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly(kotlin("stdlib"))
    }

    tasks {
        shadowJar
    }
}