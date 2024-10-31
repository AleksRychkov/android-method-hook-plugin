package io.github.aleksrychkov.methodhook.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.utils.setDisallowChanges
import io.github.aleksrychkov.methodhook.utils.Log
import io.github.aleksrychkov.methodhook.utils.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import java.io.File

/**
 * A Gradle plugin for injecting method calls into Android projects during the build process.
 */
abstract class MethodHookPlugin : Plugin<Project> {
    private companion object {
        const val EXTENSION_NAME = "androidMethodHook"
    }

    override fun apply(target: Project) {
        val extension = target.extensions.create(
            EXTENSION_NAME,
            MethodHookPluginConfig::class.java,
        )
        setup(
            target = target,
            pluginConfig = extension,
        )
    }

    private fun setup(
        target: Project,
        pluginConfig: MethodHookPluginConfig,
    ) {
        target.plugins.findPlugin(AppPlugin::class.java) ?: run {
            throw UnknownDomainObjectException("Android method hook plugin supports only android project")
        }

        val androidComponents = target.extensions.getByType(AndroidComponentsExtension::class.java)


        androidComponents.onVariants { variant ->
            Logger(target, pluginConfig.forceLogging)

            val configFiles = pluginConfig.configFiles(target = target, variant = variant)

            if (configFiles.isEmpty()) return@onVariants

            Log.i(
                "Setup injection for buildType: \"${variant.name}\" with config files:\n" +
                        configFiles.joinToString("\n")
            )

            variant.instrumentation.transformClassesWith(
                MethodHookAsmClassVisitorFactory::class.java,
                InstrumentationScope.ALL,
            ) { parameters ->
                if (pluginConfig.forceClassTransform) {
                    parameters.invalidate.setDisallowChanges(System.currentTimeMillis())
                }
                parameters.configs.setDisallowChanges(configFiles)
            }
            val computationMode = FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_CLASSES
            variant.instrumentation.setAsmFramesComputationMode(computationMode)
            variant.instrumentation.excludes.add("**`/`*Test")
        }
    }

    private fun MethodHookPluginConfig.configFiles(
        target: Project,
        variant: Variant,
    ): List<File> = configs
        .asSequence()
        .filter { conf ->
            conf.name == variant.name || variant.productFlavors.any { it.second == conf.name }
        }
        .flatMap {
            it.configs.get()
        }
        .map {
            target.file(it)
        }
        .toList()
}
