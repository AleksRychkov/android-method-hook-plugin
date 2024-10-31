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
        assertTrue(configs.first() is DescriptorConfig)
        val config = configs.first() as DescriptorConfig
        assertEquals(ConfigValue.Value("package"), config.packageId)
        assertEquals(ConfigValue.All, config.superClass)
        assertEquals(ConfigValue.Value("class"), config.clazz)
        assertEquals(ConfigValue.Value(listOf("test")), config.methods)
        assertEquals(ConfigValue.Value(listOf("interface")), config.interfaces)
        assertEquals("()V", config.descriptor)
        assertEquals("enter", config.enterInjectMethod)
        assertEquals("exit", config.exitInjectMethod)
    }
}
