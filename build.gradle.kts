import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

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
    implementation("net.analyse:sdk:1.0.4")
    implementation("redis.clients:jedis:4.2.0")

    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("org.spigotmc:spigot-api:1.18-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:23.0.0")
}

group = "net.analyse"
version = "1.0.13"
description = "Analyse"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val shadowJar: ShadowJar by tasks

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

task<com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation>("relocateShadowJar") {
    target = tasks.shadowJar.get()
    prefix = "net.analyse.plugin.libs"
    shadowJar.minimize()
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