import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;

val shadowJar: ShadowJar by tasks

task<com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation>("relocateShadowJar") {
    target = tasks.shadowJar.get()
    prefix = "net.analyse.plugin.bungee.libs"
    shadowJar.minimize()
}

tasks.shadowJar.get().dependsOn(tasks.getByName("relocateShadowJar"))

repositories {
    mavenLocal()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.maven.apache.org/maven2/")
    mavenCentral()
}

dependencies {
    implementation("redis.clients:jedis:4.2.0")
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("ninja.leaping.configurate:configurate-yaml:3.7.1")

    compileOnly("net.md-5:bungeecord-api:1.18-R0.1-SNAPSHOT")
}
