package dev.aleksrychkov.methodhook.asm

import dev.aleksrychkov.methodhook.config.MethodHookConfig
import dev.aleksrychkov.methodhook.utils.Log
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

class MethodHookClassVisitor(
    api: Int,
    cv: ClassVisitor,
    private val config: MethodHookConfig,
) : ClassVisitor(api, cv) {
    private var className: String = ""

    override fun visit(
        version: Int,
        access: Int,
        jvmName: String,
        signature: String?,
        superJvmName: String?,
        interfaceJvmNames: Array<out String>?,
    ) {
        super.visit(version, access, jvmName, signature, superJvmName, interfaceJvmNames)
        this.className = jvmName.replace("/", ".")
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?,
    ): MethodVisitor {
        var mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (config.hasMethod(name, descriptor)) {
            mv = MethodHookAdviceAdapter(
                api, mv, access, name, descriptor, className, config,
            )
        }
        return mv
    }
}
