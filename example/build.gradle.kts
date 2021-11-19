
group = "org.netherald"
version = "1.0-SNAPSHOT"

subprojects {
    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    dependencies {
        compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
        compileOnly(project(":bukkit-api"))
    }
}