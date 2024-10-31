package io.github.aleksrychkov.methodhook.injects

import io.github.aleksrychkov.methodhook.config.Config
import io.github.aleksrychkov.methodhook.config.DefaultConfig
import io.github.aleksrychkov.methodhook.config.DescriptorConfig
import io.github.aleksrychkov.methodhook.config.TraceConfig
import io.github.aleksrychkov.methodhook.injects.injectors.DefaultInjector
import io.github.aleksrychkov.methodhook.injects.injectors.DescriptorInjector
import io.github.aleksrychkov.methodhook.injects.injectors.TraceInjector

internal object InjectorFactory {
    fun get(configs: List<Config>): Set<Injector> =
        configs
            .map { config ->
                when (config) {
                    is DefaultConfig -> DefaultInjector(
                        enterInjectMethod = config.enterInjectMethod,
                        exitInjectMethod = config.exitInjectMethod,
                    )

                    is DescriptorConfig -> DescriptorInjector(
                        enterInjectMethod = config.enterInjectMethod,
                        exitInjectMethod = config.exitInjectMethod,
                    )

                    is TraceConfig -> TraceInjector
                }
            }
            .toSet()
}
