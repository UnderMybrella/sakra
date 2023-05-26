package dev.brella.sakra

import org.gradle.api.Project
import org.gradle.api.internal.tasks.JvmConstants
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.create
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import dev.brella.kornea.BuildConstants

public class SakraGradlePlugin : KotlinCompilerPluginSupportPlugin {
    public interface Ext {
    }

    override fun apply(target: Project) {
        target.extensions.create<Ext>("sakra")

        val sakraConfiguration = target.configurations.create("sakra") {
            isVisible = false
            defaultDependencies { add(target.dependencies.create("dev.brella:sakra-api:${BuildConstants.GRADLE_VERSION}")) }
        }

        target.configurations.getByName(JvmConstants.API_CONFIGURATION_NAME) {
            extendsFrom(sakraConfiguration)
        }
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
        kotlinCompilation
            .target
            .project
            .plugins
            .hasPlugin(this::class.java)

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project

        return project.provider {
            emptyList()
        }
    }

    override fun getCompilerPluginId(): String =
        "dev.brella.sakra"

    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact("dev.brella", "kornea-sakra-processor", BuildConstants.GRADLE_VERSION)
}