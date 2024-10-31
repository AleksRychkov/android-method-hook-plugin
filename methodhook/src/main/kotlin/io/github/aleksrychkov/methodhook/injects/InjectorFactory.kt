package io.github.aleksrychkov.methodhook.injects

import io.github.aleksrychkov.methodhook.config.Config
import io.github.aleksrychkov.methodhook.config.DefaultConfig
import io.github.aleksrychkov.methodhook.config.DescriptorConfig
import io.github.aleksrychkov.methodhook.config.TraceConfig
import io.github.aleksrychkov.methodhook.injects.injectors.DefaultInjector
import io.github.aleksrychkov.methodhook.injects.injectors.DescriptorInjector
import io.github.aleksrychkov.methodhook.injects.injectors.TraceInjector

/**
 * A factory object for creating instances of [Injector] based on the provided configurations.
 */
internal object InjectorFactory {

    /**
     * Retrieves a set of [Injector] instances based on the provided list of [Config] objects.
     *
     * This method examines each configuration and creates the appropriate injector instance:
     * - [DefaultInjector] for [DefaultConfig]
     * - [DescriptorInjector] for [DescriptorConfig]
     * - [TraceInjector] for [TraceConfig]
     *
     * @param configs A list of [Config] objects that dictate the behavior of the injectors to be created.
     * @return A set of [Injector] instances corresponding to the provided configurations.
     */
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
