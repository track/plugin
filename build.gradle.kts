defaultTasks("clean", "shadowJar")

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

subprojects {

    apply(plugin = "java-library")
    apply(plugin = "com.github.johnrengelman.shadow")

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    group = "net.analyse.plugin"
    version = "1.1.13"

    tasks {
        shadowJar {
            archiveFileName.set("${project.name}-analyse-${project.version}.jar")
        }
        compileJava {
            options.encoding = "UTF-8"
        }

        processResources {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE

            filesNotMatching("**/*.zip") {
                expand("pluginVersion" to version)
            }
        }
    }

    repositories {
        mavenCentral()
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://jitpack.io/")
        maven("https://libraries.minecraft.net/")
        maven("https://repo.codemc.org/repository/maven-public/")
    }
}
