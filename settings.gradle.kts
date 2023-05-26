import dev.brella.kornea.gradle.settings.includeSubprojects

pluginManagement {
    repositories {
//        maven(url = "./build/repo")
        mavenLocal()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.brella.kornea.settings") version "1.0.1"
}

rootProject.name = "sakra"

includeSubprojects(emptyList(), rootDir)
