import dev.brella.kornea.gradle.defineVersions
import dev.brella.kornea.gradle.mavenBrella

plugins {
    kotlin("multiplatform") version "1.8.10" apply false
    kotlin("jvm") version "1.8.10" apply false
    kotlin("plugin.serialization") version "1.8.10" apply false
    id("org.jetbrains.dokka") version "1.8.10" apply false
    id("com.gradle.plugin-publish") version "1.2.0" apply false

    id("dev.brella.kornea") version "2.1.0"
}

allprojects {
    repositories {
        mavenCentral()
        mavenBrella()
    }
}

configure(subprojects) {
    apply(plugin = "maven-publish")

    group = "dev.brella"
    version = "1.0.0"

    configure<PublishingExtension> {
        repositories {
            maven(url = "${rootProject.buildDir}/repo")
            mavenLocal()
        }
    }
}

defineVersions {
}