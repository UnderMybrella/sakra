import dev.brella.kornea.gradle.registerBuildConstantsTask
import org.jetbrains.kotlin.cli.common.toBooleanLenient

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
    id("com.gradle.plugin-publish")
    `kotlin-dsl`
    `java-gradle-plugin`
}

val enableDokka by enabled("dokka.enabled", "DOKKA_ENABLED")

group = "dev.brella"
version = "1.0.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
    mavenLocal()
}

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("pluginMaven")
    }
}

if (enableDokka) {
    java {
        withSourcesJar()
        withJavadocJar()
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

kotlin {
    explicitApi()
}

val main by sourceSets.getting
val test by sourceSets.getting
val integrationTest by sourceSets.creating {
    compileClasspath += main.output
    runtimeClasspath += main.output
}
val functionalTest by sourceSets.creating {
    compileClasspath += main.output
    runtimeClasspath += main.output
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
    if (enableDokka) dokkaHtmlPlugin("org.jetbrains.dokka:javadoc-plugin:1.8.10")

    arrayOf(test, integrationTest, functionalTest).forEach { src ->
        val configName = "${src.name}Implementation"

        configName(kotlin("test"))
        configName("io.kotest:kotest-framework-engine:5.5.5")
        configName("io.kotest:kotest-assertions-core:5.5.5")
        configName("io.kotest:kotest-runner-junit5:5.5.5")
    }
}

val integrationTestTask = tasks.register<Test>("integrationTest") {
    description = "Runs the integration tests."
    group = "verification"
    testClassesDirs = integrationTest.output.classesDirs
    classpath = integrationTest.runtimeClasspath
    mustRunAfter(tasks.test)
}

val functionalTestTask = tasks.register<Test>("functionalTest") {
    description = "Runs the functional tests."
    group = "verification"
    testClassesDirs = functionalTest.output.classesDirs
    classpath = functionalTest.runtimeClasspath
    mustRunAfter(tasks.test)
}

tasks.check {
    dependsOn(integrationTestTask)
    dependsOn(functionalTestTask)
}

tasks.withType<Test>().configureEach {
    // Using JUnitPlatform for running tests
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("kornea-gradle") {
            id = "dev.brella.kornea.inline"
            displayName = "Kornea Inline Operations"
            implementationClass = "dev.brella.kornea.ksp.inline.KorneaInlinePlugin"
            description = "Adds inline sequencing operations"

            tags.set(listOf("gradle", "plugin", "kotlin"))
        }
    }

    website.set("https://github.com/UnderMybrella/kornea-ksp")
    vcsUrl.set("https://github.com/UnderMybrella/kornea-ksp")
    testSourceSets(functionalTest)
}

if (enableDokka) {
    tasks.named<Jar>("javadocJar") {
        from(tasks.named("dokkaJavadoc"))
    }
}

val buildConstants = registerBuildConstantsTask("buildConstants") {
    outputDirectory.set(layout.buildDirectory.dir("generated").map { it.file("sakra") })

    gradleVersion("GRADLE_VERSION")
}

main.kotlin.srcDir(buildConstants)