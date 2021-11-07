plugins {
    id("com.google.devtools.ksp")
    kotlin("jvm")
}

group = "org.netherald"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":api"))
}
