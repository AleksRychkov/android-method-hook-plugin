package io.github.aleksrychkov.methodhook.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigRenderOptions

internal class ConfigMapper {

    private companion object {
        const val PATH_TYPE = "type"
        const val PATH_PACKAGE = "package"
        const val PATH_SUPER_CLASS = "superClass"
        const val PATH_INTERFACES = "interfaces"
        const val PATH_CLASS = "class"
        const val PATH_METHODS = "methods"
        const val PATH_ENTER = "enter"
        const val PATH_EXIT = "exit"
        const val PATH_DESCRIPTOR = "descriptor"

        const val TYPE_TRACE = "trace"
        const val TYPE_DEFAULT = "default"
        const val TYPE_DESCRIPTOR = "descriptor"

        const val VALUE_ALL = "*"
    }

    private val availableTypes = arrayOf(TYPE_DEFAULT, TYPE_TRACE, TYPE_DESCRIPTOR)

    @Suppress("UseCheckOrError")
    fun map(config: Config): io.github.aleksrychkov.methodhook.config.Config {
        config.checkRequiredFields()

        val type = config.getString(PATH_TYPE).trim().lowercase()
        check(availableTypes.contains(type)) {
            "Given type `$type` is not supported. Available types are: ${availableTypes.joinToString()}"
        }

        return when (type) {
            TYPE_TRACE -> mapTrace(config = config)
            TYPE_DEFAULT -> mapDefault(config = config)
            TYPE_DESCRIPTOR -> mapDescriptor(config = config)
            else -> throw IllegalStateException("Impossible")
        }
    }

    private fun mapTrace(config: Config): io.github.aleksrychkov.methodhook.config.Config = TraceConfig(
        packageId = config.stringValue(PATH_PACKAGE),
        superClass = config.stringValue(PATH_SUPER_CLASS),
        interfaces = config.optionalListStringValue(PATH_INTERFACES),
        clazz = config.stringValue(PATH_CLASS),
        methods = config.listStringValue(PATH_METHODS),
    )

    private fun mapDefault(config: Config): io.github.aleksrychkov.methodhook.config.Config = DefaultConfig(
        packageId = config.stringValue(PATH_PACKAGE),
        superClass = config.stringValue(PATH_SUPER_CLASS),
        interfaces = config.optionalListStringValue(PATH_INTERFACES),
        clazz = config.stringValue(PATH_CLASS),
        methods = config.listStringValue(PATH_METHODS),
        enterInjectMethod = config.stringOrNull(PATH_ENTER),
        exitInjectMethod = config.stringOrNull(PATH_EXIT),
    )

    private fun mapDescriptor(config: Config): io.github.aleksrychkov.methodhook.config.Config = DescriptorConfig(
        packageId = config.stringValue(PATH_PACKAGE),
        superClass = config.stringValue(PATH_SUPER_CLASS),
        interfaces = config.optionalListStringValue(PATH_INTERFACES),
        clazz = config.stringValue(PATH_CLASS),
        methods = config.listStringValue(PATH_METHODS),
        enterInjectMethod = config.stringOrNull(PATH_ENTER),
        exitInjectMethod = config.stringOrNull(PATH_EXIT),
        descriptor = config.stringOrNull(PATH_DESCRIPTOR),
    )

    private fun Config.stringValue(path: String): ConfigValue<String> {
        val value = this.getString(path)
        return if (value == VALUE_ALL) {
            ConfigValue.All
        } else {
            ConfigValue.Value(value = value)
        }
    }

    private fun Config.listStringValue(path: String): ConfigValue<List<String>> {
        val value = this.getStringList(path)
        return if (value.isEmpty()) {
            ConfigValue.All
        } else {
            ConfigValue.Value(value = value)
        }
    }

    private fun Config.optionalListStringValue(path: String): ConfigValue<List<String>> {
        if (!this.hasPath(path)) {
            return ConfigValue.All
        }
        val value = this.getStringList(path)
        return if (value.isEmpty()) {
            ConfigValue.All
        } else {
            ConfigValue.Value(value = value)
        }
    }

    private fun Config.stringOrNull(path: String): String? =
        if (this.hasPath(path)) {
            this.getString(path)
        } else {
            null
        }

    private fun Config.checkRequiredFields() {
        fun checkPath(path: String) {
            check(this.hasPath(path)) {
                val configJson = this.root().render(ConfigRenderOptions.concise())
                "`$path` is missing in config: $configJson"
            }
            check(!this.getString(path).isNullOrBlank()) {
                val configJson = this.root().render(ConfigRenderOptions.concise())
                "Invalid value of `$path` parameter in config: $configJson"
            }
        }

        checkPath(PATH_TYPE)
        checkPath(PATH_PACKAGE)
        checkPath(PATH_SUPER_CLASS)
        checkPath(PATH_CLASS)

        check(this.hasPath(PATH_METHODS)) {
            val configJson = this.root().render(ConfigRenderOptions.concise())
            "`$PATH_METHODS` is missing in config: $configJson"
        }

        check(this.hasPath(PATH_INTERFACES)) {
            val configJson = this.root().render(ConfigRenderOptions.concise())
            "`$PATH_INTERFACES` is missing in config: $configJson"
        }
    }
}
