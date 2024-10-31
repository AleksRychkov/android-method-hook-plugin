package io.github.aleksrychkov.methodhook.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigParseOptions
import io.github.aleksrychkov.methodhook.TestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class ConfigMapperTest {

    private val configFile = TestUtils.resource("test_mapper.conf")
    private val config = ConfigFactory
        .parseFile(configFile, ConfigParseOptions.defaults().setAllowMissing(false))
        .resolve()
    private val configs: Map<String, Config> =
        config.root().keys.associateWith { path -> config.getConfig(path) }
    private val mapper = ConfigMapper()

    @Nested
    inner class FieldType {

        @ParameterizedTest
        @ValueSource(strings = ["opt_out_type", "empty_type", "unknown_type"])
        fun `Given invalid type When map called Then exception thrown`(input: String) {
            // Given
            val config = requireNotNull(configs[input])
            // When
            val exception: IllegalStateException? = try {
                mapper.map(config)
                null
            } catch (e: IllegalStateException) {
                e
            }
            // Then
            assertNotNull(exception)
            println(exception)
        }

        @ParameterizedTest
        @ValueSource(strings = ["typeTrace", "typeDefault", "typeDescriptor"])
        fun `When map called Then result is of correct type`(input: String) {
            // Given
            val config = requireNotNull(configs[input])
            val expected = when (input) {
                "typeTrace" -> MethodHookTraceConfig::class.java
                "typeDefault" -> MethodHookDefaultConfig::class.java
                "typeDescriptor" -> MethodHookDescriptorConfig::class.java
                else -> null
            }
            // When
            val actual = mapper.map(config)
            // Then
            assertEquals(expected, actual::class.java)
        }
    }

    @Nested
    inner class FieldPackage {

        @ParameterizedTest
        @ValueSource(strings = ["opt_out_package", "empty_package"])
        fun `Given invalid package When map called Then exception thrown`(input: String) {
            // Given
            val config = requireNotNull(configs[input])
            // When
            val exception: IllegalStateException? = try {
                mapper.map(config)
                null
            } catch (e: IllegalStateException) {
                e
            }
            // Then
            assertNotNull(exception)
            println(exception)
        }

        @ParameterizedTest
        @ValueSource(strings = ["all_package", "value_package"])
        fun `When map called Then output packageId is valid`(input: String) {
            // Given
            val config = requireNotNull(configs[input])
            val expected = if (input == "all_package") {
                MethodHookConfigValue.All
            } else {
                MethodHookConfigValue.Value("package")
            }
            // When
            val actual = mapper.map(config)
            // Then
            assertEquals(expected, actual.packageId)
        }
    }

    @Nested
    inner class FieldSuperClass {

        @ParameterizedTest
        @ValueSource(strings = ["opt_out_super_class", "empty_super_class"])
        fun `Given invalid superClass When map called Then exception thrown`(input: String) {
            // Given
            val config = requireNotNull(configs[input])
            // When
            val exception: IllegalStateException? = try {
                mapper.map(config)
                null
            } catch (e: IllegalStateException) {
                e
            }
            // Then
            assertNotNull(exception)
            println(exception)
        }

        @ParameterizedTest
        @ValueSource(strings = ["all_super_class", "value_super_class"])
        fun `When map called Then output superClass is valid`(input: String) {
            // Given
            val config = requireNotNull(configs[input])
            val expected = if (input == "all_super_class") {
                MethodHookConfigValue.All
            } else {
                MethodHookConfigValue.Value("superClass")
            }
            // When
            val actual = mapper.map(config)
            // Then
            assertEquals(expected, actual.superClass)
        }
    }

    @Nested
    inner class FieldClass {

        @ParameterizedTest
        @ValueSource(strings = ["opt_out_class", "empty_class"])
        fun `Given invalid class When map called Then exception thrown`(input: String) {
            // Given
            val config = requireNotNull(configs[input])
            // When
            val exception: IllegalStateException? = try {
                mapper.map(config)
                null
            } catch (e: IllegalStateException) {
                e
            }
            // Then
            assertNotNull(exception)
            println(exception)
        }

        @ParameterizedTest
        @ValueSource(strings = ["all_class", "value_class"])
        fun `When map called Then output clazz is valid`(input: String) {
            // Given
            val config = requireNotNull(configs[input])
            val expected = if (input == "all_class") {
                MethodHookConfigValue.All
            } else {
                MethodHookConfigValue.Value("class")
            }
            // When
            val actual = mapper.map(config)
            // Then
            assertEquals(expected, actual.clazz)
        }
    }

    @Nested
    inner class FieldMethods {

        @ParameterizedTest
        @ValueSource(strings = ["opt_out_methods", "invalid_methods"])
        fun `Given invalid methods When map called Then exception thrown`(input: String) {
            // Given
            val config = requireNotNull(configs[input])
            // When
            val exception: Exception? = try {
                mapper.map(config)
                null
            } catch (e: Exception) {
                e
            }
            // Then
            assertNotNull(exception)
            println(exception)
        }

        @ParameterizedTest
        @ValueSource(strings = ["all_methods", "value_methods"])
        fun `When map called Then output methods is valid`(input: String) {
            // Given
            val config = requireNotNull(configs[input])
            val expected = if (input == "all_methods") {
                MethodHookConfigValue.All
            } else {
                MethodHookConfigValue.Value(listOf("method"))
            }
            // When
            val actual = mapper.map(config)
            // Then
            assertEquals(expected, actual.methods)
        }
    }

    @Nested
    inner class FieldInterfaces {

        @ParameterizedTest
        @ValueSource(strings = ["opt_out_interfaces", "invalid_interfaces"])
        fun `Given invalid interfaces When map called Then exception thrown`(input: String) {
            // Given
            val config = requireNotNull(configs[input])
            // When
            val exception: Exception? = try {
                mapper.map(config)
                null
            } catch (e: Exception) {
                e
            }
            // Then
            assertNotNull(exception)
            println(exception)
        }

        @ParameterizedTest
        @ValueSource(strings = ["all_interfaces", "value_interfaces"])
        fun `When map called Then output interfaces is valid`(input: String) {
            // Given
            val config = requireNotNull(configs[input])
            val expected = if (input == "all_interfaces") {
                MethodHookConfigValue.All
            } else {
                MethodHookConfigValue.Value(listOf("interface"))
            }
            // When
            val actual = mapper.map(config)
            // Then
            assertEquals(expected, actual.interfaces)
        }
    }
}
