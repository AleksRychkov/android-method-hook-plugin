package dev.aleksrychkov.methodhook.config

import dev.aleksrychkov.methodhook.config.MethodHookConfigValidator.Companion.BEGIN_METHOD_AND_END_METHOD_MISSING_ERROR
import dev.aleksrychkov.methodhook.config.MethodHookConfigValidator.Companion.DUPLICATE_ERROR
import dev.aleksrychkov.methodhook.config.MethodHookConfigValidator.Companion.INVALID_PACKAGE_ID
import dev.aleksrychkov.methodhook.config.MethodHookConfigValidator.Companion.METHODS_EMPTY
import dev.aleksrychkov.methodhook.config.MethodHookConfigValidator.Companion.SUPER_AND_EXACT_MISSING_ERROR
import dev.aleksrychkov.methodhook.config.MethodHookConfigValidator.Companion.SUPER_AND_EXACT_SET_ERROR
import dev.aleksrychkov.methodhook.helpers.AndroidMethodInjectorInceptionConfigFactory.configInstance
import dev.aleksrychkov.methodhook.helpers.AndroidMethodInjectorInceptionConfigFactory.methodInstance
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MethodHookConfigValidatorTest {

    private val validator: MethodHookConfigValidator = MethodHookConfigValidator()

    @Test
    fun `When validate and super and exact classes are missing Then exception is thrown`() {
        // Given
        val config = configInstance()

        // When
        val error: IllegalStateException? = try {
            validator.validate(listOf(config))
            null
        } catch (e: IllegalStateException) {
            e
        }

        // Then
        assertNotNull(error)
        requireNotNull(error)
        assertTrue(error.message!!.startsWith(SUPER_AND_EXACT_MISSING_ERROR))
    }

    @Test
    fun `When validate and super and exact classes are set Then exception is thrown`() {
        // Given
        val config = configInstance(exactClass = "exact", superClass = "super")

        // When
        val error: IllegalStateException? = try {
            validator.validate(listOf(config))
            null
        } catch (e: IllegalStateException) {
            e
        }

        // Then
        assertNotNull(error)
        requireNotNull(error)
        assertTrue(error.message!!.startsWith(SUPER_AND_EXACT_SET_ERROR))
    }

    @Test
    fun `When validate and methods are empty Then exception is thrown`() {
        // Given
        val config = configInstance(exactClass = "exact")

        // When
        val error: IllegalStateException? = try {
            validator.validate(listOf(config))
            null
        } catch (e: IllegalStateException) {
            e
        }

        // Then
        assertNotNull(error)
        requireNotNull(error)
        assertTrue(error.message!!.startsWith(METHODS_EMPTY))
    }

    @Test
    fun `When validate and methods contains empty strings Then exception is thrown`() {
        // Given
        val config = configInstance(exactClass = "exact", methods = setOf(methodInstance()))

        // When
        val error: IllegalStateException? = try {
            validator.validate(listOf(config))
            null
        } catch (e: IllegalStateException) {
            e
        }

        // Then
        assertNotNull(error)
        requireNotNull(error)
        assertTrue(error.message!!.startsWith(METHODS_EMPTY))
    }

    @Test
    fun `When validate and methods contains empty method Then exception is thrown`() {
        // Given
        val config = configInstance(
            exactClass = "exact",
            methods = setOf(methodInstance()),
        )

        // When
        val error: IllegalStateException? = try {
            validator.validate(listOf(config))
            null
        } catch (e: IllegalStateException) {
            e
        }

        // Then
        assertNotNull(error)
        requireNotNull(error)
        assertTrue(error.message!!.startsWith(METHODS_EMPTY))
    }

    @Test
    fun `When validate and beginMethodWith and endMethodWith are missing Then exception is thrown`() {
        // Given
        val config = configInstance(
            exactClass = "exact",
            methods = setOf(methodInstance("method")),
        )

        // When
        val error: IllegalStateException? = try {
            validator.validate(listOf(config))
            null
        } catch (e: IllegalStateException) {
            e
        }

        // Then
        assertNotNull(error)
        requireNotNull(error)
        assertTrue(error.message!!.startsWith(BEGIN_METHOD_AND_END_METHOD_MISSING_ERROR))
    }

    @Test
    fun `When validate and configs has duplicate exactClass Then exception is thrown`() {
        // Given
        val config = configInstance(
            exactClass = "exact",
            methods = setOf(methodInstance("method")),
            beginMethodWith = "method",
        )
        val configDuplicate = configInstance(
            exactClass = "exact",
            methods = setOf(methodInstance("method")),
            beginMethodWith = "method",
        )

        // When
        val error: IllegalStateException? = try {
            validator.validate(listOf(config, configDuplicate))
            null
        } catch (e: IllegalStateException) {
            e
        }

        // Then
        assertNotNull(error)
        requireNotNull(error)
        assertEquals(DUPLICATE_ERROR.format("exactClass", "exact"), error.message!!)
    }

    @Test
    fun `When validate and configs has duplicate superClass Then exception is thrown`() {
        // Given
        val config = configInstance(
            superClass = "super",
            methods = setOf(methodInstance("method")),
            beginMethodWith = "method",
        )
        val configDuplicate = configInstance(
            superClass = "super",
            methods = setOf(methodInstance("method")),
            beginMethodWith = "method",
        )

        // When
        val error: IllegalStateException? = try {
            validator.validate(listOf(config, configDuplicate))
            null
        } catch (e: IllegalStateException) {
            e
        }

        // Then
        assertNotNull(error)
        requireNotNull(error)
        assertEquals(DUPLICATE_ERROR.format("superClass", "super"), error.message!!)
    }

    @Test
    fun `When validate and config has invalid packageId Then exception is thrown`() {
        // Given
        val config = configInstance(
            superClass = "super",
            methods = setOf(methodInstance("method")),
            beginMethodWith = "method",
            packageId = "123InvalidPackage"
        )

        // When
        val error: IllegalStateException? = try {
            validator.validate(listOf(config))
            null
        } catch (e: IllegalStateException) {
            e
        }

        // Then
        assertNotNull(error)
        requireNotNull(error)
        assertTrue(error.message!!.startsWith(INVALID_PACKAGE_ID))
    }

    @Test
    fun `When validate and config has packageId with whitespace only Then exception is thrown`() {
        // Given
        val config = configInstance(
            superClass = "super",
            methods = setOf(methodInstance("method")),
            beginMethodWith = "method",
            packageId = " "
        )

        // When
        val error: IllegalStateException? = try {
            validator.validate(listOf(config))
            null
        } catch (e: IllegalStateException) {
            e
        }

        // Then
        assertNotNull(error)
        requireNotNull(error)
        assertTrue(error.message!!.startsWith(INVALID_PACKAGE_ID))
    }

    @Test
    fun `When validate Then no exception is thrown`() {
        // Given
        val config = configInstance(
            superClass = "super",
            methods = setOf(methodInstance("method")),
            beginMethodWith = "method",
        )

        // When
        val error: IllegalStateException? = try {
            validator.validate(listOf(config))
            null
        } catch (e: IllegalStateException) {
            e
        }

        // Then
        assertNull(error)
    }

    @Test
    fun `When validate and configs has duplicate but different packageId Then no exception is thrown`() {
        // Given
        val config = configInstance(
            superClass = "super",
            methods = setOf(methodInstance("method")),
            beginMethodWith = "method",
            packageId = "package1",
        )
        val config2 = configInstance(
            superClass = "super",
            methods = setOf(methodInstance("method")),
            beginMethodWith = "method",
            packageId = "package2",
        )

        // When
        val error: IllegalStateException? = try {
            validator.validate(listOf(config, config2))
            null
        } catch (e: IllegalStateException) {
            e
        }

        // Then
        assertNull(error)
    }
}
