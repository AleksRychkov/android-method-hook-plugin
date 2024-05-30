package io.github.aleksrychkov.methodhook.config

import io.github.aleksrychkov.methodhook.config.MethodHookConfig.Companion.CONF_BEGIN_METHOD_WITH
import io.github.aleksrychkov.methodhook.config.MethodHookConfig.Companion.CONF_END_METHOD_WITH
import io.github.aleksrychkov.methodhook.config.MethodHookConfig.Companion.CONF_EXACT_CLASS
import io.github.aleksrychkov.methodhook.config.MethodHookConfig.Companion.CONF_METHODS
import io.github.aleksrychkov.methodhook.config.MethodHookConfig.Companion.CONF_SUPER_CLASS
import io.github.aleksrychkov.methodhook.config.MethodHookConfigValidator.Companion.BEGIN_METHOD_AND_END_METHOD_MISSING_ERROR
import io.github.aleksrychkov.methodhook.config.MethodHookConfigValidator.Companion.DUPLICATE_ERROR
import io.github.aleksrychkov.methodhook.config.MethodHookConfigValidator.Companion.INVALID_PACKAGE_ID
import io.github.aleksrychkov.methodhook.config.MethodHookConfigValidator.Companion.METHODS_EMPTY
import io.github.aleksrychkov.methodhook.config.MethodHookConfigValidator.Companion.SUPER_AND_EXACT_MISSING_ERROR
import io.github.aleksrychkov.methodhook.config.MethodHookConfigValidator.Companion.SUPER_AND_EXACT_SET_ERROR

interface MethodHookConfigValidator {

    companion object {
        const val INVALID_PACKAGE_ID = "Invalid packageId."

        const val SUPER_AND_EXACT_MISSING_ERROR =
            "One of the following parameters must be set: [$CONF_SUPER_CLASS], [$CONF_EXACT_CLASS]."

        const val SUPER_AND_EXACT_SET_ERROR =
            "Only one of the following parameters must be set: [$CONF_SUPER_CLASS], [$CONF_EXACT_CLASS]."

        const val METHODS_EMPTY =
            "Required parameter not set or empty: [$CONF_METHODS]"

        const val BEGIN_METHOD_AND_END_METHOD_MISSING_ERROR =
            "At least one of the following parameters must be set: [$CONF_BEGIN_METHOD_WITH], [$CONF_END_METHOD_WITH]."

        const val DUPLICATE_ERROR =
            "Configs with the same [%s] parameter not allowed. Duplicate value is: %s"
    }

    @Throws(IllegalStateException::class)
    fun validate(configs: List<MethodHookConfig>)
}

private class MethodHookConfigValidatorImpl : MethodHookConfigValidator {

    private val packageNameRegex = Regex("^[a-zA-Z][a-zA-Z0-9_]*(\\.[a-zA-Z0-9_]+)*$")

    override fun validate(configs: List<MethodHookConfig>) {
        configs.groupBy { it.packageId }.values.forEach { configsByPackageId ->
            configsByPackageId.forEach { conf ->
                val errorDetail = " For config: $conf"

                check(conf.validatePackageId()) {
                    INVALID_PACKAGE_ID + errorDetail
                }

                check(conf.superAndExactClassesMissing().not()) {
                    SUPER_AND_EXACT_MISSING_ERROR + errorDetail
                }

                check(conf.superAndExactClassesSet().not()) {
                    SUPER_AND_EXACT_SET_ERROR + errorDetail
                }

                check(conf.methodsPresent()) {
                    METHODS_EMPTY + errorDetail
                }

                check(conf.beginOrEndWithMethodPresent()) {
                    BEGIN_METHOD_AND_END_METHOD_MISSING_ERROR + errorDetail
                }
            }

            checkDuplicateParameter(configsByPackageId, CONF_EXACT_CLASS) { it.exactClass }

            checkDuplicateParameter(configsByPackageId, CONF_SUPER_CLASS) { it.superClass }
        }
    }

    private fun MethodHookConfig.validatePackageId() =
        this.packageId.isEmpty() || packageNameRegex.matches(this.packageId)

    private fun MethodHookConfig.superAndExactClassesMissing() =
        this.superClass.isBlank() && this.exactClass.isBlank()

    private fun MethodHookConfig.superAndExactClassesSet() =
        this.superClass.isNotBlank() && this.exactClass.isNotBlank()

    private fun MethodHookConfig.methodsPresent() =
        this.methods.isNotEmpty() && this.methods.none { it.name.isBlank() }

    private fun MethodHookConfig.beginOrEndWithMethodPresent() =
        this.beginMethodWith.isNotBlank() || this.endMethodWith.isNotBlank()

    private fun checkDuplicateParameter(
        configs: List<MethodHookConfig>,
        param: String,
        map: (MethodHookConfig) -> String,
    ) {
        val nonEmptyParamConfigs = configs.map(map).filter { it.isNotEmpty() }
        val nonEmptyParamConfigsSet = nonEmptyParamConfigs.toSet()
        val hasDuplicates = nonEmptyParamConfigs.size != nonEmptyParamConfigsSet.size
        check(hasDuplicates.not()) {
            DUPLICATE_ERROR.format(param, nonEmptyParamConfigsSet.joinToString())
        }
    }
}

private var instance: MethodHookConfigValidator? = null

fun MethodHookConfigValidator(): MethodHookConfigValidator =
    instance ?: MethodHookConfigValidatorImpl().also { instance = it }
