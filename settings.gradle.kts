pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.minecraftforge.net/") { name = "MinecraftForge" }
        maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.8"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

stonecutter.create(rootProject) {
    versions(
        mapOf(
            "6.9.0" to "6.9.0",
            "6.17.0" to "6.17.0",
        ),
    )
    vcsVersion.set("6.9.0")
}

rootProject.name = "GeoTetraArmor"
