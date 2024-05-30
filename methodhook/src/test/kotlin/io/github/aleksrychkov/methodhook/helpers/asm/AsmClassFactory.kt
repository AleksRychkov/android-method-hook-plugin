package io.github.aleksrychkov.methodhook.helpers.asm

import io.github.aleksrychkov.methodhook.helpers.TestUtils
import io.github.aleksrychkov.methodhook.config.MethodHookConfig
import java.lang.reflect.Constructor

object AsmClassFactory {
    fun createInstance(
        className: String,
        config: MethodHookConfig,
    ): Pair<Class<*>, Any> {
        val cl = AsmClassLoader(className, config)
        val clazz = cl.findClass(className)
        val constructor: Constructor<*> = clazz.getConstructor()
        val instance = constructor.newInstance()
        return clazz to instance
    }
}


private class AsmClassLoader(
    private val className: String,
    private val config: MethodHookConfig,
) : ClassLoader() {
    public override fun findClass(name: String): Class<*> {
        if (className == name) {
            val bytecode = TestUtils.loadBytecode(name)
            val transformed = TestUtils.applyTransformation(bytecode, config)
            return defineClass(name, transformed, 0, transformed.size)
        }
        return super.findClass(name)
    }
}
