import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;

val shadowJar: ShadowJar by tasks

task<com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation>("relocateShadowJar") {
    target = tasks.shadowJar.get()
    prefix = "net.analyse.plugin.bukkit.libs"
    shadowJar.relocate("net.analyse.sdk", "net.analyse.sdk")
    shadowJar.minimize()
}

tasks.shadowJar.get().dependsOn(tasks.getByName("relocateShadowJar"))

repositories {
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.maven.apache.org/maven2/")
    mavenCentral()
}

dependencies {
    implementation(project(":sdk"))

    implementation("net.sf.trove4j:trove4j:3.0.3")
    implementation("redis.clients:jedis:4.2.0") {
        exclude("com.google.code.gson", "gson")
    }
    implementation("org.slf4j:slf4j-simple:1.7.36")

    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("org.spigotmc:spigot-api:1.18-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:23.0.0")
}
