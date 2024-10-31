package io.github.aleksrychkov.methodhook.injects.injectors

import io.github.aleksrychkov.methodhook.injects.Injector
import io.github.aleksrychkov.methodhook.injects.InjectorContext
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.ACC_STATIC
import org.objectweb.asm.Opcodes.ACONST_NULL
import org.objectweb.asm.Opcodes.ALOAD
import org.objectweb.asm.Opcodes.ARETURN
import org.objectweb.asm.Opcodes.ATHROW
import org.objectweb.asm.Opcodes.DLOAD
import org.objectweb.asm.Opcodes.DRETURN
import org.objectweb.asm.Opcodes.FLOAD
import org.objectweb.asm.Opcodes.FRETURN
import org.objectweb.asm.Opcodes.ILOAD
import org.objectweb.asm.Opcodes.INVOKESTATIC
import org.objectweb.asm.Opcodes.IRETURN
import org.objectweb.asm.Opcodes.LLOAD
import org.objectweb.asm.Opcodes.LRETURN
import org.objectweb.asm.Opcodes.RETURN
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

class DescriptorInjector(
    private val enterInjectMethod: String?,
    private val exitInjectMethod: String?,
) : Injector {
    override fun onEnter(context: InjectorContext, mv: MethodVisitor) = with(mv) {
        if (enterInjectMethod == null) return@with

        val argTypes = Type.getArgumentTypes(context.methodDescriptor)
        var localVarIndex = if ((context.access and ACC_STATIC) == 0) 1 else 0

        for (type in argTypes) {
            when (type.sort) {
                Type.INT -> visitVarInsn(ILOAD, localVarIndex)
                Type.FLOAT -> visitVarInsn(FLOAD, localVarIndex)
                Type.LONG -> visitVarInsn(LLOAD, localVarIndex)
                Type.DOUBLE -> visitVarInsn(DLOAD, localVarIndex)
                Type.OBJECT, Type.ARRAY -> visitVarInsn(ALOAD, localVarIndex)
                Type.BOOLEAN, Type.BYTE, Type.CHAR, Type.SHORT ->
                    visitVarInsn(ILOAD, localVarIndex)

                else -> throw IllegalArgumentException("Unsupported argument type: ${type.descriptor}")
            }
            localVarIndex += type.size
        }
        val injectDescriptor = context.methodDescriptor.substringBeforeLast(")") + ")V"
        visitMethodInsn(
            INVOKESTATIC,
            enterInjectMethod.substringBeforeLast(".").replace(".", "/"),
            enterInjectMethod.substringAfterLast("."),
            injectDescriptor,
            false,
        )
    }

    override fun onExit(
        opcode: Int,
        context: InjectorContext,
        mv: MethodVisitor,
        adviceAdapter: AdviceAdapter
    ) = with(mv) {
        if (exitInjectMethod == null) return@with

        if (opcode == RETURN) {
            mv.visitInsn(ACONST_NULL)
        } else {
            when (opcode) {
                IRETURN -> {
                    adviceAdapter.dup()
                    adviceAdapter.box(Type.getType(Int::class.java))
                }

                FRETURN -> {
                    adviceAdapter.dup()
                    adviceAdapter.box(Type.getType(Float::class.java))
                }

                LRETURN -> {
                    adviceAdapter.dup2()
                    adviceAdapter.box(Type.getType(Long::class.java))
                }

                DRETURN -> {
                    adviceAdapter.dup2()
                    adviceAdapter.box(Type.getType(Double::class.java))
                }

                ARETURN, ATHROW -> mv.visitInsn(Opcodes.DUP)
            }
        }

        visitMethodInsn(
            INVOKESTATIC,
            exitInjectMethod.substringBeforeLast(".").replace(".", "/"),
            exitInjectMethod.substringAfterLast("."),
            "(Ljava/lang/Object;)V",
            false,
        )
    }
}
