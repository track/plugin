import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

repositories {
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.maven.apache.org/maven2/")
    mavenCentral()
}

dependencies {
    val sdkVersion = project.property("analyze.sdk.hash")

    implementation("net.sf.trove4j:trove4j:3.0.3")
    implementation("com.github.track:sdk:$sdkVersion")

    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("org.spigotmc:spigot-api:1.18-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:23.0.0")
}

group = "net.analyse"
version = "1.0.11"
description = "Analyse"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val shadowJar: ShadowJar by tasks

task<com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation>("relocateShadowJar") {
    target = tasks.shadowJar.get()
    prefix = "net.analyse.plugin.libs"
}

tasks.shadowJar.get().dependsOn(tasks.getByName("relocateShadowJar"))

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
