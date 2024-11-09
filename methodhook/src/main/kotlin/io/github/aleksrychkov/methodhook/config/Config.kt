package io.github.aleksrychkov.methodhook.config

/**
 * A sealed interface representing a configuration for method injection.
 *
 * This interface defines the common properties that all configuration types must implement.
 * These properties include identifiers for packages, superclasses, interfaces, classes, and methods.
 */
internal sealed interface Config {
    val packageId: ConfigValue<String>
    val superClass: ConfigValue<String>
    val interfaces: ConfigValue<List<String>>
    val clazz: ConfigValue<String>
    val methods: ConfigValue<List<String>>
}

/**
 * A data class representing a tracing configuration.
 *
 * This configuration is used for specifying method injection settings related to tracing.
 * Methods will be instrumented with `android.os.Trace.beginSection` and `android.os.Trace.endSection`
 * calls respectively.
 */
internal data class TraceConfig(
    override val packageId: ConfigValue<String>,
    override val superClass: ConfigValue<String>,
    override val interfaces: ConfigValue<List<String>>,
    override val clazz: ConfigValue<String>,
    override val methods: ConfigValue<List<String>>,
    val traceMsgPrefix: String? = null
) : Config

/**
 * A data class representing the default configuration for method injection.
 *
 * This configuration specifies the default method injection settings.
 * Methods will be instrumented with provided [enterInjectMethod] and [exitInjectMethod] calls, respectively.
 */
internal data class DefaultConfig(
    override val packageId: ConfigValue<String>,
    override val superClass: ConfigValue<String>,
    override val interfaces: ConfigValue<List<String>>,
    override val clazz: ConfigValue<String>,
    override val methods: ConfigValue<List<String>>,
    val enterInjectMethod: String? = null,
    val exitInjectMethod: String? = null,
) : Config

/**
 * A data class representing a descriptor configuration for method injection.
 *
 * This configuration is similar to the default configuration but includes an optional method
 * descriptor property.
 * Methods will be instrumented with provided [enterInjectMethod] and [exitInjectMethod] calls, respectively.
 * [enterInjectMethod] will be called with instrumented method arguments.
 * [exitInjectMethod] will be called with instrumented method result type casted to [Object] class.
 */
internal data class DescriptorConfig(
    override val packageId: ConfigValue<String>,
    override val superClass: ConfigValue<String>,
    override val interfaces: ConfigValue<List<String>>,
    override val clazz: ConfigValue<String>,
    override val methods: ConfigValue<List<String>>,
    val enterInjectMethod: String? = null,
    val exitInjectMethod: String? = null,
    val descriptor: String? = null,
) : Config

/**
 * A sealed interface representing a value configuration.
 *
 * This interface can either represent a specific value or indicate that all values are accepted.
 * It is used in conjunction with the `Config` interface to provide flexibility in matching.
 */
internal sealed interface ConfigValue<out T> {
    data object All : ConfigValue<Nothing>
    data class Value<T>(val value: T) : ConfigValue<T>
}

internal fun ConfigValue<*>.isAll(): Boolean =
    this is ConfigValue.All

internal fun <T> ConfigValue<T>.valueOrNull(): T? =
    (this as? ConfigValue.Value<T>)?.value

internal fun <T> ConfigValue<T>.valueOrThrow(): T =
    requireNotNull(this.valueOrNull())
