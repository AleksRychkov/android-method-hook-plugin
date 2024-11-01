package io.github.aleksrychkov.methodhook.injects

/**
 * A context class that holds information about the method being modified by an injector.
 *
 * @property className The name of the class that contains the method being injected.
 * @property methodName The name of the method being injected.
 * @property methodDescriptor The descriptor of the method being injected,
 *           providing information about the method's parameter and return types.
 * @property access The access modifiers of the method, represented as an integer (e.g., public, private).
 */
class InjectorContext(
    val className: String,
    val methodName: String,
    val methodDescriptor: String,
    val access: Int,
)
