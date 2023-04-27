import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(project(":sdk"))
    implementation("it.unimi.dsi:fastutil:8.5.6")

    compileOnly("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
    compileOnly("dev.dejvokep:boosted-yaml:1.3")
    compileOnly("me.clip:placeholderapi:2.11.3")
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