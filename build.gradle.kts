
plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("kapt") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("com.google.devtools.ksp")
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.5.31"))
    }
}

group = "org.netherald"
version = "1.0-SNAPSHOT"

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "com.github.johnrengelman.shadow")

    if (name == "annotation-processor") {
        apply(plugin = "com.google.devtools.ksp")
        apply(plugin = "org.jetbrains.kotlin.kapt")
    }

    if (name != "common") {
        var continueThis = false
        parent?.let {
            if (it.name == "example") continueThis = true
        }
        if (!continueThis) {
            dependencies {
                implementation(project(":common"))
            }
        }
    }

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
}