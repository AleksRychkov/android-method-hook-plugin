package io.github.aleksrychkov.methodhook.config

import com.typesafe.config.Config

object MethodHookConfigMapper {

    fun map(config: Config) = MethodHookConfig(
        superClass = config.stringOrDefault(MethodHookConfig.CONF_SUPER_CLASS),
        exactClass = config.stringOrDefault(MethodHookConfig.CONF_EXACT_CLASS),
        methods = config.methodsOrEmpty(MethodHookConfig.CONF_METHODS),
        beginMethodWith = config.stringOrDefault(MethodHookConfig.CONF_BEGIN_METHOD_WITH),
        endMethodWith = config.stringOrDefault(MethodHookConfig.CONF_END_METHOD_WITH),
        packageId = config.stringOrDefault(MethodHookConfig.CONF_PACKAGE_ID),
    )

    private fun Config.stringOrDefault(
        path: String,
        default: String = "",
    ): String = if (this.hasPath(path)) this.getString(path) else default

    private fun Config.methodsOrEmpty(path: String): Set<MethodHookConfig.Method> =
        if (this.hasPath(path)) {
            this.getStringList(path)
                .map { input ->
                    val method = if (input.contains("(")) {
                        input.substringBefore("(", "")
                    } else {
                        input
                    }
                    MethodHookConfig.Method(method)
                }
                .toSet()
        } else {
            emptySet()
        }
}
