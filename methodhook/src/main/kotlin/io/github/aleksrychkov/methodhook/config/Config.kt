package io.github.aleksrychkov.methodhook.config

internal sealed interface MethodHookConfig {
    val packageId: MethodHookConfigValue<String>
    val superClass: MethodHookConfigValue<String>
    val interfaces: MethodHookConfigValue<List<String>>
    val clazz: MethodHookConfigValue<String>
    val methods: MethodHookConfigValue<List<String>>
}

internal data class MethodHookTraceConfig(
    override val packageId: MethodHookConfigValue<String>,
    override val superClass: MethodHookConfigValue<String>,
    override val interfaces: MethodHookConfigValue<List<String>>,
    override val clazz: MethodHookConfigValue<String>,
    override val methods: MethodHookConfigValue<List<String>>,
) : MethodHookConfig

internal data class MethodHookDefaultConfig(
    override val packageId: MethodHookConfigValue<String>,
    override val superClass: MethodHookConfigValue<String>,
    override val interfaces: MethodHookConfigValue<List<String>>,
    override val clazz: MethodHookConfigValue<String>,
    override val methods: MethodHookConfigValue<List<String>>,
    val enterInjectMethod: String? = null,
    val exitInjectMethod: String? = null,
) : MethodHookConfig

internal data class MethodHookDescriptorConfig(
    override val packageId: MethodHookConfigValue<String>,
    override val superClass: MethodHookConfigValue<String>,
    override val interfaces: MethodHookConfigValue<List<String>>,
    override val clazz: MethodHookConfigValue<String>,
    override val methods: MethodHookConfigValue<List<String>>,
    val enterInjectMethod: String? = null,
    val exitInjectMethod: String? = null,
    val descriptor: String? = null,
) : MethodHookConfig

internal sealed interface MethodHookConfigValue<out T> {
    data object All : MethodHookConfigValue<Nothing>
    data class Value<T>(val value: T) : MethodHookConfigValue<T>
}

internal fun MethodHookConfigValue<*>.isAll(): Boolean =
    this is MethodHookConfigValue.All

internal fun <T> MethodHookConfigValue<T>.valueOrNull(): T? =
    (this as? MethodHookConfigValue.Value<T>)?.value

internal fun <T> MethodHookConfigValue<T>.valueOrThrow(): T =
    requireNotNull(this.valueOrNull())
