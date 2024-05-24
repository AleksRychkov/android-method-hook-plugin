package dev.aleksrychkov.methodhook.asm

import dev.aleksrychkov.methodhook.config.MethodHookConfig
import dev.aleksrychkov.methodhook.utils.Log
import dev.aleksrychkov.methodhook.utils.convertDescriptor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

class MethodHookAdviceAdapter(
    api: Int,
    mv: MethodVisitor,
    access: Int,
    name: String,
    methodDescriptor: String,
    private val className: String,
    private val config: MethodHookConfig,
) : AdviceAdapter(api, mv, access, name, methodDescriptor) {

    private companion object {
        const val JVM_THROWABLE_TYPE = "java/lang/Exception"
    }

    private val start: Label = Label()
    private val end: Label = Label()

    private var _fullMethod: String? = null
    private val fullMethod: String
        get() {
            if (_fullMethod == null) {
                _fullMethod = name + methodDesc.convertDescriptor()
            }
            return _fullMethod!!
        }

    override fun onMethodEnter() {
        visitTryCatchBlock(start, end, end, JVM_THROWABLE_TYPE)
        visitLabel(start)

        execBeforeBlock()
    }

    override fun visitMaxs(
        maxStack: Int,
        maxLocals: Int,
    ) {
        visitLabel(end)

        execAfterBlock()

        visitInsn(Opcodes.ATHROW)
        super.visitMaxs(maxStack, maxLocals)
    }

    override fun onMethodExit(opcode: Int) {
        if (opcode == Opcodes.ATHROW) {
            return
        }
        execAfterBlock()
    }

    private fun execBeforeBlock() {
        if (config.beginMethodWith.isNotEmpty()) {
            Log.i("Perform inject onMethodEnter for $className on $fullMethod with: ${config.beginMethodWith}")
            injectCode(config.beginMethodWith)
        }
    }

    private fun execAfterBlock() {
        if (config.endMethodWith.isNotEmpty()) {
            Log.i("Perform inject onMethodExit for $className on $fullMethod with: ${config.endMethodWith}")
            injectCode(config.endMethodWith)
        }
    }

    private fun injectCode(method: String) {
        visitVarInsn(Opcodes.ALOAD, 0)
        visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/lang/Object",
            "getClass",
            "()Ljava/lang/Class;",
            false,
        )
        visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/lang/Class",
            "getName",
            "()Ljava/lang/String;",
            false,
        )
        val classNameLocalVariableIndex = newLocal(Type.getType("Ljava/lang/String;"))
        visitVarInsn(Opcodes.ASTORE, classNameLocalVariableIndex)
        visitVarInsn(Opcodes.ALOAD, classNameLocalVariableIndex)

        visitLdcInsn(className)
        visitLdcInsn(fullMethod)

        visitMethodInsn(
            Opcodes.INVOKESTATIC,
            method.substringBeforeLast(".").replace(".", "/"),
            method.substringAfterLast("."),
            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
            false,
        )
    }
}
