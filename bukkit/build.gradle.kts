import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = rootProject.group
version = rootProject.version

repositories {
    maven {
        url = uri("https://repo.opencollab.dev/maven-snapshots/")
    }
}

dependencies {
    implementation(project(":sdk"))
    implementation("it.unimi.dsi:fastutil:8.5.6")

    compileOnly("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
    compileOnly("dev.dejvokep:boosted-yaml:1.3")
    compileOnly("me.clip:placeholderapi:2.11.3")
    compileOnly("org.geysermc.floodgate:api:2.2.0-SNAPSHOT")
}

tasks.named("shadowJar", ShadowJar::class.java) {
    configurations = listOf(project.configurations.runtimeClasspath.get())

    relocate("it.unimi", "net.analyse.plugin.libs.fastutil")
    relocate("okhttp3", "net.analyse.plugin.libs.okhttp3")
    relocate("okio", "net.analyse.plugin.libs.okio")
    relocate("dev.dejvokep.boostedyaml", "net.analyse.plugin.libs.boostedyaml")
    relocate("org.jetbrains.annotations", "net.analyse.plugin.libs.jetbrains")
    relocate("kotlin", "net.analyse.plugin.libs.kotlin")
    minimize()
}

tasks.register("copyToServer", Copy::class.java) {
    from(project.tasks.named("shadowJar").get().outputs)
    into("/Users/charlie/Documents/LegacyMCServer/plugins")

    // rely on the shadowJar task to build the jar
    dependsOn("shadowJar")
}