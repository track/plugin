plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}

rootProject.name = "AnalysePlugin"

listOf("sdk", "bukkit", "bungeecord", "velocity").forEach {
    include(it)
}