import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = rootProject.group
version = rootProject.version

repositories {
    maven {
        url = uri("https://repo.opencollab.dev/maven-snapshots/")
    }
    maven {
        url = uri("https://mvn-repo.arim.space/lesser-gpl3/")
    }
}

dependencies {
    implementation(project(":sdk"))
    implementation("it.unimi.dsi:fastutil:8.5.6")
    implementation("space.arim.morepaperlib:morepaperlib:0.4.3")

    compileOnly("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
    compileOnly("dev.dejvokep:boosted-yaml:1.3")
    compileOnly("me.clip:placeholderapi:2.11.3")
    compileOnly("org.geysermc.floodgate:api:2.2.0-SNAPSHOT")
}

tasks.named("shadowJar", ShadowJar::class.java) {
    configurations = listOf(project.configurations.runtimeClasspath.get())

    relocate("it.unimi", "io.tebex.analytics.libs.fastutil")
    relocate("okhttp3", "io.tebex.analytics.libs.okhttp3")
    relocate("okio", "io.tebex.analytics.libs.okio")
    relocate("dev.dejvokep.boostedyaml", "io.tebex.analytics.libs.boostedyaml")
    relocate("org.jetbrains.annotations", "io.tebex.analytics.libs.jetbrains")
    relocate("space.arim.morepaperlib", "io.tebex.analytics.libs.paperlib")
    relocate("kotlin", "io.tebex.analytics.libs.kotlin")
    minimize()
}

tasks.register("copyToServer", Copy::class.java) {
    from(project.tasks.named("shadowJar").get().outputs)
    into("/Users/charlie/Documents/LegacyMCServer/plugins")

    // rely on the shadowJar task to build the jar
    dependsOn("shadowJar")
}