package io.github.aleksrychkov.methodhook.injects

import io.github.aleksrychkov.methodhook.config.MethodHookConfig
import io.github.aleksrychkov.methodhook.config.MethodHookDefaultConfig
import io.github.aleksrychkov.methodhook.config.MethodHookDescriptorConfig
import io.github.aleksrychkov.methodhook.config.MethodHookTraceConfig
import io.github.aleksrychkov.methodhook.injects.injectors.DefaultInjector
import io.github.aleksrychkov.methodhook.injects.injectors.DescriptorInjector
import io.github.aleksrychkov.methodhook.injects.injectors.TraceInjector

internal object InjectorFactory {
    fun get(configs: List<MethodHookConfig>): Set<Injector> =
        configs
            .map { config ->
                when (config) {
                    is MethodHookDefaultConfig -> DefaultInjector(
                        enterInjectMethod = config.enterInjectMethod,
                        exitInjectMethod = config.exitInjectMethod,
                    )

                    is MethodHookDescriptorConfig -> DescriptorInjector(
                        enterInjectMethod = config.enterInjectMethod,
                        exitInjectMethod = config.exitInjectMethod,
                    )

                    is MethodHookTraceConfig -> TraceInjector
                }
            }
            .toSet()
}
