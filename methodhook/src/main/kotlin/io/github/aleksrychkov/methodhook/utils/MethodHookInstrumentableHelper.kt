package io.github.aleksrychkov.methodhook.utils

import com.android.build.api.instrumentation.ClassData
import io.github.aleksrychkov.methodhook.config.MethodHookConfig

object MethodHookInstrumentableHelper {

    fun List<MethodHookConfig>.findInstrumentable(
        classData: ClassData,
    ): MethodHookConfig? =
        this.let { list ->
            list.searchByExactClass(classData)
                ?: list.searchByPackageIdAndSuperClass(classData)
                ?: list.searchBySuperClass(classData)
        }

    private fun List<MethodHookConfig>.searchByExactClass(
        classData: ClassData
    ): MethodHookConfig? =
        this.firstOrNull { it.instrumentableByExactClass(classData) }

    private fun List<MethodHookConfig>.searchByPackageIdAndSuperClass(
        classData: ClassData
    ): MethodHookConfig? {
        val configs = this.filter {
            it.packageId.isNotEmpty() &&
                    classData.className.fromPackage(it.packageId) &&
                    it.instrumentableBySuperClass(classData)
        }

        checkMultipleConfigsRestriction(configs, classData)

        return configs.firstOrNull()
    }

    private fun List<MethodHookConfig>.searchBySuperClass(
        classData: ClassData
    ): MethodHookConfig? {
        val configs = this.filter {
            it.packageId.isEmpty() && it.instrumentableBySuperClass(classData)
        }

        checkMultipleConfigsRestriction(configs, classData)

        return configs.firstOrNull()
    }

    private fun MethodHookConfig.instrumentableByExactClass(classData: ClassData): Boolean =
        this.exactClass.isNotEmpty() && classData.className.endsWith(this.exactClass)

    private fun MethodHookConfig.instrumentableBySuperClass(classData: ClassData): Boolean =
        this.superClass.isNotEmpty() && classData.superClasses.contains(this.superClass)

    private fun String.fromPackage(packageId: String): Boolean = this.startsWith("$packageId.")

    private fun checkMultipleConfigsRestriction(
        configs: List<MethodHookConfig>,
        classData: ClassData
    ) {
        check(configs.size <= 1) {
            "Multiple configs are found for ${classData.className}.\n" +
                    "Configs: ${configs.joinToString("\n")}"
        }
    }
}
