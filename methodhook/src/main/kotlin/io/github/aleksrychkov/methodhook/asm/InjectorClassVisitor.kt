package io.github.aleksrychkov.methodhook.asm

import io.github.aleksrychkov.methodhook.config.MethodHookConfig
import io.github.aleksrychkov.methodhook.config.MethodHookDescriptorConfig
import io.github.aleksrychkov.methodhook.config.isAll
import io.github.aleksrychkov.methodhook.config.valueOrThrow
import io.github.aleksrychkov.methodhook.injects.InjectorFactory
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import kotlin.properties.Delegates.notNull

internal class InjectorClassVisitor(
    api: Int,
    cv: ClassVisitor,
    private val configs: List<MethodHookConfig>,
) : ClassVisitor(api, cv) {

    private var className: String by notNull()

    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        className = name
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        var mv = super.visitMethod(access, name, descriptor, signature, exceptions)

        val injectors = configs
            .asSequence()
            .filter { config ->
                config.methods.isAll() || config.methods.valueOrThrow().contains(name)
            }
            .filter { config ->
                if (config is MethodHookDescriptorConfig) {
                    config.descriptor == descriptor
                } else {
                    true
                }
            }
            .toList()
            .let(InjectorFactory::get)


        if (injectors.isNotEmpty()) {
            mv = TryFinallyAdapter(
                injectors = injectors,
                className = className,
                api = api,
                mv = mv,
                access = access,
                name = name,
                descriptor = descriptor,
            )
        }
        return mv
    }
}
