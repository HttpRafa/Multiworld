plugins {
    id("java")
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation("org.jetbrains:annotations:" + findProperty("jetbrains_annotations"))
    implementation("commons-io:commons-io:" + findProperty("commons_io"))

    compileOnly("org.spigotmc:spigot-api:" + findProperty("spigot_version"))

    compileOnly("org.projectlombok:lombok:" + findProperty("lombok_version"))
    annotationProcessor("org.projectlombok:lombok:" + findProperty("lombok_version"))
}