@file:Suppress("SwallowedException")

package io.github.aleksrychkov.methodhook.asm

import io.github.aleksrychkov.methodhook.helpers.AndroidMethodInjectorInceptionConfigFactory.configInstance
import io.github.aleksrychkov.methodhook.helpers.asm.AsmClassFactory
import io.github.aleksrychkov.methodhook.helpers.asm.SimpleSample
import io.github.aleksrychkov.methodhook.helpers.asm.TestInjector
import io.github.aleksrychkov.methodhook.utils.MethodHookLogger
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

class MethodHookClassVisitorTest {

    @Nested
    inner class TransformationTest {

        @BeforeEach
        fun setup() {
            MethodHookLogger.stub()
            TestInjector.start("", "", "")
            TestInjector.end("", "", "")
        }

        @Test
        fun `When classMethod called Then injected beginMethodWith and endMethodWith executed`() {
            // Given
            val className = SimpleSample.className
            val classMethod = "simpleMethod"
            val calledClassMethod = "$classMethod()->void"
            val config = configInstance(
                methods = SimpleSample.methods,
                beginMethodWith = TestInjector.startMethod,
                endMethodWith = TestInjector.endMethod,
            )
            val (clazz, instance) = AsmClassFactory.createInstance(className, config)

            // When
            clazz.invokeMethod(classMethod, instance)

            // Then
            assertTrue(TestInjector.startWasCalledFor(className, calledClassMethod))
            assertTrue(TestInjector.endWasCalledFor(className, calledClassMethod))
        }

        @Test
        fun `When classMethod called and method throws exception Then injected beginMethodWith and endMethodWith executed`() {
            // Given
            val className = SimpleSample.className
            val classMethod = "exceptionMethod"
            val calledClassMethod = "$classMethod()->void"
            val config = configInstance(
                methods = SimpleSample.methods,
                beginMethodWith = TestInjector.startMethod,
                endMethodWith = TestInjector.endMethod,
            )
            val (clazz, instance) = AsmClassFactory.createInstance(className, config)

            // When
            assertThrows<Exception> { clazz.invokeMethod(classMethod, instance) }

            // Then
            assertTrue(TestInjector.startWasCalledFor(className, calledClassMethod))
            assertTrue(TestInjector.endWasCalledFor(className, calledClassMethod))
        }

        @Test
        fun `When classMethod called and only beginMethodWith configured Then only injected beginMethodWith executed`() {
            // Given
            val className = SimpleSample.className
            val classMethod = "simpleMethod"
            val calledClassMethod = "$classMethod()->void"
            val config = configInstance(
                methods = SimpleSample.methods,
                beginMethodWith = TestInjector.startMethod,
            )
            val (clazz, instance) = AsmClassFactory.createInstance(className, config)

            // When
            clazz.invokeMethod(classMethod, instance)

            // Then
            assertTrue(TestInjector.startWasCalledFor(className, calledClassMethod))
            assertFalse(TestInjector.endWasCalledFor(className, calledClassMethod))
        }

        @Test
        fun `When classMethod called and only endMethodWith configured Then only injected endMethodWith executed`() {
            // Given
            val className = SimpleSample.className
            val classMethod = "simpleMethod"
            val calledClassMethod = "$classMethod()->void"
            val config = configInstance(
                methods = SimpleSample.methods,
                endMethodWith = TestInjector.endMethod,
            )
            val (clazz, instance) = AsmClassFactory.createInstance(className, config)

            // When
            clazz.invokeMethod(classMethod, instance)

            // Then
            assertFalse(TestInjector.startWasCalledFor(className, calledClassMethod))
            assertTrue(TestInjector.endWasCalledFor(className, calledClassMethod))
        }

        private fun Class<*>.invokeMethod(
            method: String,
            instance: Any,
        ) {
            this.getMethod(method).invoke(instance)
        }
    }

    @Nested
    inner class ClassVisitorTest {

        @BeforeEach
        fun setup() {
            MethodHookLogger.stub()
            TestInjector.start("", "", "")
            TestInjector.end("", "", "")
        }

        @Test
        fun `When visitMethod called and method is not configured Then result is not AndroidMethodInjectorAdviceAdapter`() {
            // Given
            val config = configInstance()
            val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES)
            val cv = object : ClassVisitor(Opcodes.ASM6, cw) {}
            val methodCv = MethodHookClassVisitor(Opcodes.ASM6, cv, config)

            // When
            val method = SimpleSample.methods.first()
            val actual =
                methodCv.visitMethod(Opcodes.ASM6, method.name, "()V", null, null)

            // Then
            assertFalse(actual is MethodHookAdviceAdapter)
        }

        @Test
        fun `When visitMethod called and method is configured Then result is AndroidMethodInjectorAdviceAdapter`() {
            // Given
            val config = configInstance(methods = SimpleSample.methods)
            val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES)
            val cv = object : ClassVisitor(Opcodes.ASM6, cw) {}
            val methodCv = MethodHookClassVisitor(Opcodes.ASM6, cv, config)

            // When
            val method = SimpleSample.methods.first()
            val actual =
                methodCv.visitMethod(Opcodes.ASM6, method.name, "()V", null, null)

            // Then
            assertTrue(actual is MethodHookAdviceAdapter)
        }
    }
}
