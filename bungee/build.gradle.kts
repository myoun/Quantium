
group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly("net.md-5:bungeecord-api:1.17-R0.1-SNAPSHOT")
    implementation("io.lettuce:lettuce-core:6.1.5.RELEASE")
    implementation(project(":common"))
    implementation(project(":bungee-api"))
    implementation(kotlin("stdlib"))
}