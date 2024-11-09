package io.github.aleksrychkov.methodhook.config

import com.typesafe.config.ConfigRenderOptions
import com.typesafe.config.Config as TypeSafeConfig

/**
 * A class responsible for mapping configuration data from typesafe format to [Config] types format.
 */
internal class ConfigMapper {

    private companion object {
        // Required field paths
        const val PATH_TYPE = "type"
        const val PATH_PACKAGE = "package"
        const val PATH_SUPER_CLASS = "superClass"
        const val PATH_INTERFACES = "interfaces"
        const val PATH_CLASS = "class"
        const val PATH_METHODS = "methods"

        // Specific field paths
        const val PATH_ENTER = "enter"
        const val PATH_EXIT = "exit"
        const val PATH_DESCRIPTOR = "descriptor"
        const val PATH_TRACE_PREFIX = "msgPrefix"

        // Supported configuration types
        const val TYPE_TRACE = "trace"
        const val TYPE_DEFAULT = "default"
        const val TYPE_DESCRIPTOR = "descriptor"

        // Special value indicating all configurations are accepted
        const val VALUE_ALL = "*"
    }

    private val availableTypes = arrayOf(TYPE_DEFAULT, TYPE_TRACE, TYPE_DESCRIPTOR)

    /**
     * Maps the provided configuration to a specific configuration type.
     *
     * This method validates the configuration and checks for required fields.
     * It determines the type of configuration and calls the appropriate mapping function
     * to create and return a corresponding [Config] instance.
     *
     * @param config The configuration to be mapped.
     * @return A mapped [Config] instance based on the provided configuration.
     * @throws IllegalArgumentException If the configuration type is not supported or required fields are missing.
     */
    @Suppress("UseCheckOrError")
    fun map(config: TypeSafeConfig): Config {
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

    private fun mapTrace(config: TypeSafeConfig): Config =
        TraceConfig(
            packageId = config.stringValue(PATH_PACKAGE),
            superClass = config.stringValue(PATH_SUPER_CLASS),
            interfaces = config.optionalListStringValue(PATH_INTERFACES),
            clazz = config.stringValue(PATH_CLASS),
            methods = config.listStringValue(PATH_METHODS),
            traceMsgPrefix = config.stringOrNull(PATH_TRACE_PREFIX),
        )

    private fun mapDefault(config: TypeSafeConfig): Config =
        DefaultConfig(
            packageId = config.stringValue(PATH_PACKAGE),
            superClass = config.stringValue(PATH_SUPER_CLASS),
            interfaces = config.optionalListStringValue(PATH_INTERFACES),
            clazz = config.stringValue(PATH_CLASS),
            methods = config.listStringValue(PATH_METHODS),
            enterInjectMethod = config.stringOrNull(PATH_ENTER),
            exitInjectMethod = config.stringOrNull(PATH_EXIT),
        )

    private fun mapDescriptor(config: TypeSafeConfig): Config =
        DescriptorConfig(
            packageId = config.stringValue(PATH_PACKAGE),
            superClass = config.stringValue(PATH_SUPER_CLASS),
            interfaces = config.optionalListStringValue(PATH_INTERFACES),
            clazz = config.stringValue(PATH_CLASS),
            methods = config.listStringValue(PATH_METHODS),
            enterInjectMethod = config.stringOrNull(PATH_ENTER),
            exitInjectMethod = config.stringOrNull(PATH_EXIT),
            descriptor = config.stringOrNull(PATH_DESCRIPTOR),
        )

    private fun TypeSafeConfig.stringValue(path: String): ConfigValue<String> {
        val value = this.getString(path)
        return if (value == VALUE_ALL) {
            ConfigValue.All
        } else {
            ConfigValue.Value(value = value)
        }
    }

    private fun TypeSafeConfig.listStringValue(path: String): ConfigValue<List<String>> {
        val value = this.getStringList(path)
        return if (value.isEmpty()) {
            ConfigValue.All
        } else {
            ConfigValue.Value(value = value)
        }
    }

    private fun TypeSafeConfig.optionalListStringValue(path: String): ConfigValue<List<String>> {
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

    private fun TypeSafeConfig.stringOrNull(path: String): String? =
        if (this.hasPath(path)) {
            this.getString(path)
        } else {
            null
        }

    private fun TypeSafeConfig.checkRequiredFields() {
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
