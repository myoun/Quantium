
group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.onarandombox.com/content/groups/public/")
}

dependencies {
    compileOnly("com.onarandombox.multiversecore:Multiverse-Core:4.2.2")
    compileOnly("com.onarandombox.multiversenetherportals:Multiverse-NetherPortals:4.2.1")
    implementation(project(":api"))
    implementation(kotlin("stdlib"))
}