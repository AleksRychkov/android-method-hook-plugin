package io.github.aleksrychkov.methodhook.helpers

import io.github.aleksrychkov.methodhook.asm.MethodHookClassVisitor
import io.github.aleksrychkov.methodhook.config.MethodHookConfig
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.util.Textifier
import org.objectweb.asm.util.TraceClassVisitor
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

object TestUtils {

    private fun String.prepareClassname() = this.replace(".", "/") + ".class"

    fun loadBytecode(className: String): ByteArray =
        this.javaClass.classLoader
            .getResourceAsStream(className.prepareClassname())!!
            .readBytes()

    fun applyTransformation(
        bytecode: ByteArray,
        config: MethodHookConfig,
    ): ByteArray {
        val cr = ClassReader(bytecode)
        val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES)
        val cv = object : ClassVisitor(Opcodes.ASM6, cw) {}
        val methodCv = MethodHookClassVisitor(Opcodes.ASM6, cv, config)
        cr.accept(methodCv, 0x0)
        return cw.toByteArray()
    }

    fun resource(name: String): File {
        val url = this::class.java.classLoader.getResource(name)!!
        return File(url.path)
    }

    @Suppress("unused")
    fun printBytecode(bytecode: ByteArray) {
        val sw = StringWriter()
        val tcv = TraceClassVisitor(null, Textifier(), PrintWriter(sw))
        val cr = ClassReader(bytecode)
        cr.accept(tcv, 0x0)
        println(sw.toString())
    }
}
