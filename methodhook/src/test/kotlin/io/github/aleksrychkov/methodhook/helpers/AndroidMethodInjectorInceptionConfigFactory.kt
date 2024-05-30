package io.github.aleksrychkov.methodhook.helpers

import io.github.aleksrychkov.methodhook.config.MethodHookConfig

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
        superClass = superClass,
        exactClass = exactClass,
        methods = methods,
        packageId = packageId,
        beginMethodWith = beginMethodWith,
        endMethodWith = endMethodWith,
    )

    fun methodInstance(name: String = "") = MethodHookConfig.Method(name)
}
