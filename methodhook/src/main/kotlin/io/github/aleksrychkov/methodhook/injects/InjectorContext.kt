package io.github.aleksrychkov.methodhook.injects

class InjectorContext(
    val className: String,
    val methodName: String,
    val methodDescriptor: String,
    val access: Int,
)
