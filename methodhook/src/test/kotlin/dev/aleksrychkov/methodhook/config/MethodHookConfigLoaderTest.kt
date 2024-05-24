package dev.aleksrychkov.methodhook.config

import dev.aleksrychkov.methodhook.helpers.TestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MethodHookConfigLoaderTest {
    private companion object {
        const val TEST_SUPER_CLASS = "SuperClass"
        const val TEST_EXACT_CLASS = "ExactClass"
        const val TEST_METHOD = "method"
        const val TEST_PACKAGE_ID = "dev.aleksrychkov.test"
        const val TEST_BEGIN_METHOD_WITH = "dev.aleksrychkov.test.begin"
        const val TEST_END_METHOD_WITH = "dev.aleksrychkov.test.end"
    }

    private val configLoader = MethodHookConfigLoader()

    @Test
    fun `When loadConfig Then result is valid`() {
        // Given
        val configFile = TestUtils.resource("test.conf")

        // When
        val inceptionConfigs = configLoader.loadConfigs(listOf(configFile))

        // Then
        assertTrue(inceptionConfigs.isNotEmpty())
        val config = inceptionConfigs.first()
        assertEquals(TEST_SUPER_CLASS, config.superClass)
        assertEquals(TEST_EXACT_CLASS, config.exactClass)
        assertEquals(TEST_METHOD, config.methods.first().name)
        assertEquals(TEST_PACKAGE_ID, config.packageId)
        assertEquals(TEST_BEGIN_METHOD_WITH, config.beginMethodWith)
        assertEquals(TEST_END_METHOD_WITH, config.endMethodWith)
    }
}
