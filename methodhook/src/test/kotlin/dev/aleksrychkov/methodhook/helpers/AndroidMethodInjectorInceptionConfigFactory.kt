package dev.aleksrychkov.methodhook.helpers

import dev.aleksrychkov.methodhook.config.MethodHookConfig

object AndroidMethodInjectorInceptionConfigFactory {

    @Suppress("LongParameterList")
    fun configInstance(
        superClass: String = "",
        exactClass: String = "",
        methods: Set<MethodHookConfig.Method> = setOf(),
        beginMethodWith: String = "",
        endMethodWith: String = "",
        packageId: String = "",
    ) = MethodHookConfig(
        buildType = "",
        superClass = superClass,
        exactClass = exactClass,
        methods = methods,
        packageId = packageId,
        beginMethodWith = beginMethodWith,
        endMethodWith = endMethodWith,
    )

    fun methodInstance(name: String = "", descriptor: String = "") =
        MethodHookConfig.Method(name, descriptor)
}
