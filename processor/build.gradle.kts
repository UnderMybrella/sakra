plugins {
    kotlin("jvm")
    `maven-publish`
}

group = "dev.brella"
version = "1.0.0"

repositories {
    mavenCentral()
}

val main by sourceSets.getting
val test by sourceSets.getting

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.8.10")
}

kotlin {
    jvmToolchain(11)
}

configure<PublishingExtension> {
    repositories {
        maven(url = "${rootProject.buildDir}/repo")
        mavenLocal()
    }

    publications {
        create<MavenPublication>("processorMaven") {
            from(components.getByName("java"))
        }
    }
}