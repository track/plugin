import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;

val shadowJar: ShadowJar by tasks

task<com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation>("relocateShadowJar") {
    target = tasks.shadowJar.get()
    prefix = "net.analyse.sdk"
    shadowJar.minimize()
}

tasks.shadowJar.get().dependsOn(tasks.getByName("relocateShadowJar"))

repositories {
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.maven.apache.org/maven2/")
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    compileOnly("com.google.code.gson:gson:2.9.0")
    compileOnly("org.jetbrains:annotations:23.0.0")
}