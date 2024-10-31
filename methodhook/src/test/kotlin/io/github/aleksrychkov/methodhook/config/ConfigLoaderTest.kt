package io.github.aleksrychkov.methodhook.config

import io.github.aleksrychkov.methodhook.TestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class ConfigLoaderTest {

    private val configLoader = ConfigLoader()

    @Test
    fun `When loadConfig Then result is valid`() {
        // Given
        val configFile = TestUtils.resource("test.conf")
        // When
        val configs = configLoader.load(listOf(configFile))
        // Then
        assertTrue(configs.isNotEmpty())
        assertTrue(configs.first() is MethodHookDescriptorConfig)
        val config = configs.first() as MethodHookDescriptorConfig
        assertEquals(MethodHookConfigValue.Value("package"), config.packageId)
        assertEquals(MethodHookConfigValue.All, config.superClass)
        assertEquals(MethodHookConfigValue.Value("class"), config.clazz)
        assertEquals(MethodHookConfigValue.Value(listOf("test")), config.methods)
        assertEquals(MethodHookConfigValue.Value(listOf("interface")), config.interfaces)
        assertEquals("()V", config.descriptor)
        assertEquals("enter", config.enterInjectMethod)
        assertEquals("exit", config.exitInjectMethod)
    }
}
