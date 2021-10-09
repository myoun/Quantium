plugins {
    kotlin("jvm") version "1.5.10"
}

group = "org.netherald"
version = "1.0-SNAPSHOT"

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    if (name == "bukkit" || name == "api") {
        repositories {
            maven("https://papermc.io/repo/repository/maven-public/")
        }

        dependencies {
            compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
        }
    }

    dependencies {
        if (name != "common") {
            implementation(project(":common"))
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}