import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;

val shadowJar: ShadowJar by tasks

task<com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation>("relocateShadowJar") {
    target = tasks.shadowJar.get()
    prefix = "net.analyse.plugin.velocity.libs"
    shadowJar.minimize()
}

tasks.shadowJar.get().dependsOn(tasks.getByName("relocateShadowJar"))

repositories {
    mavenLocal()
    maven("https://nexus.velocitypowered.com/repository/maven-public/")
    maven("https://repo.maven.apache.org/maven2/")
    mavenCentral()
}

dependencies {
    implementation("redis.clients:jedis:4.2.0")
    implementation("org.slf4j:slf4j-simple:1.7.36")

    compileOnly("com.velocitypowered:velocity-api:3.1.0")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.0")
}
