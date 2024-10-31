package io.github.aleksrychkov.methodhook.config

internal sealed interface Config {
    val packageId: ConfigValue<String>
    val superClass: ConfigValue<String>
    val interfaces: ConfigValue<List<String>>
    val clazz: ConfigValue<String>
    val methods: ConfigValue<List<String>>
}

internal data class TraceConfig(
    override val packageId: ConfigValue<String>,
    override val superClass: ConfigValue<String>,
    override val interfaces: ConfigValue<List<String>>,
    override val clazz: ConfigValue<String>,
    override val methods: ConfigValue<List<String>>,
) : Config

internal data class DefaultConfig(
    override val packageId: ConfigValue<String>,
    override val superClass: ConfigValue<String>,
    override val interfaces: ConfigValue<List<String>>,
    override val clazz: ConfigValue<String>,
    override val methods: ConfigValue<List<String>>,
    val enterInjectMethod: String? = null,
    val exitInjectMethod: String? = null,
) : Config

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
