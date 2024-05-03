package dev.aleksrychkov.methodhook.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.utils.setDisallowChanges
import dev.aleksrychkov.methodhook.utils.Log
import dev.aleksrychkov.methodhook.utils.MethodHookLogger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException

abstract class MethodHookPlugin : Plugin<Project> {
    private companion object {
        const val EXTENSION_NAME = "androidMethodHook"
    }

    override fun apply(target: Project) {
        val extension = target.extensions.create(
            EXTENSION_NAME,
            MethodHookPluginConfig::class.java,
        )
        setup(target, extension)
    }

    private fun setup(
        target: Project,
        extension: MethodHookPluginConfig,
    ) {
        target.plugins.findPlugin(AppPlugin::class.java) ?: run {
            throw UnknownDomainObjectException("Android method hook plugin supports only android project")
        }

        val androidComponents = target.extensions.getByType(AndroidComponentsExtension::class.java)

        androidComponents.onVariants { variant ->
            MethodHookLogger(target, extension.forceLogging)

            val configFiles = extension.configs
                .filter { conf ->
                    conf.name == variant.name || variant.productFlavors.any { it.second == conf.name }
                }
                .flatMap {
                    it.sources.get()
                }
                .map {
                    target.file(it)
                }

            if (configFiles.isEmpty()) return@onVariants
            println()
            Log.i(
                "Setup injection for buildType: \"${variant.name}\" with config files:\n" +
                        configFiles.joinToString("\n")
            )
            println()

            variant.instrumentation.transformClassesWith(
                MethodHookAsmClassVisitorFactory::class.java,
                InstrumentationScope.ALL,
            ) { parameters ->
                if (extension.forceClassTransform) {
                    parameters.invalidate.setDisallowChanges(System.currentTimeMillis())
                }
                parameters.configs.setDisallowChanges(configFiles)
            }
            val computationMode = FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
            variant.instrumentation.setAsmFramesComputationMode(computationMode)
        }
    }
}
