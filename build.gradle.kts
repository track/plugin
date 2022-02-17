import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

repositories {
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.maven.apache.org/maven2/")
    mavenCentral()
}

dependencies {
    implementation("net.sf.trove4j:trove4j:3.0.3")
    implementation("com.maxmind.geoip2:geoip2:2.16.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("org.spigotmc:spigot-api:1.18-R0.1-SNAPSHOT")
    compileOnly("com.google.code.gson:gson:2.8.9")
    compileOnly("org.jetbrains:annotations:16.0.2")
}

group = "net.analyse"
version = "1.0.3"
description = "Analyse"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val shadowJar: ShadowJar by tasks

tasks.withType<ProcessResources> {
    filesMatching("**/plugin.yml") {
        expand("version" to project.version)
    }
}

tasks.register<Copy>("copyJarToServerPlugins") {
    from(tasks.getByPath("shadowJar"))
    into(layout.projectDirectory.dir("server/plugins"))
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}