package io.github.aleksrychkov.methodhook

import org.objectweb.asm.ClassReader
import org.objectweb.asm.util.Textifier
import org.objectweb.asm.util.TraceClassVisitor
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

internal object TestUtils {

    private fun String.prepareClassname() = this.replace(".", "/") + ".class"

    @Suppress("unused")
    fun loadBytecode(className: String): ByteArray =
        this.javaClass.classLoader
            .getResourceAsStream(className.prepareClassname())!!
            .readBytes()

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
