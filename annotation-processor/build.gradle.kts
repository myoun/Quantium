
val kspVersion: String by project

group = "org.netherald"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/kotlin-dev")
}

dependencies {
    implementation("org.yaml:snakeyaml:1.25")
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
    implementation(kotlin("stdlib"))
}