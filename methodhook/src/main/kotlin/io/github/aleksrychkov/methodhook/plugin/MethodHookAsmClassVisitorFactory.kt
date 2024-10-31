package io.github.aleksrychkov.methodhook.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import io.github.aleksrychkov.methodhook.asm.InjectorClassVisitor
import io.github.aleksrychkov.methodhook.config.Config
import io.github.aleksrychkov.methodhook.config.ConfigLoader
import io.github.aleksrychkov.methodhook.config.isAll
import io.github.aleksrychkov.methodhook.config.valueOrThrow
import org.objectweb.asm.ClassVisitor
import java.util.Collections

/**
 * An abstract factory class for creating ASM Class Visitors that handle
 * method hooking instrumentation based on specified configuration parameters.
 */
internal abstract class MethodHookAsmClassVisitorFactory :
    AsmClassVisitorFactory<MethodHookInstrumentationParameters> {

    companion object {
        private var cache = Collections.synchronizedMap(LruCache<String, List<Config>?>())
        private var configs: List<Config> = emptyList()
        private var isConfigLoaded = false
    }

    /**
     * Creates a [ClassVisitor] that can be used to instrument a class.
     *
     * This method retrieves the configuration for the current class and wraps the
     * next class visitor with an [InjectorClassVisitor] if applicable. The class
     * visitor is responsible for applying the specified instrumentation to the class
     * being processed.
     *
     * @param classContext The [ClassContext] containing information about the class being visited.
     * @param nextClassVisitor The next class visitor in the chain to which this visitor will delegate.
     * @return A [ClassVisitor] that wraps the provided class visitor for method instrumentation.
     */
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor,
    ): ClassVisitor {
        var cv = nextClassVisitor
        val configs = cache[classContext.currentClassData.className]
            ?: getConfigForClassData(classContext.currentClassData)

        if (configs.isNotEmpty()) {
            val api = instrumentationContext.apiVersion.get()
            cv = InjectorClassVisitor(
                api = api,
                cv = cv,
                configs = configs,
            )
        }
        return cv
    }

    /**
     * Determines whether the specified class can be instrumented.
     *
     * This method checks if there are any applicable configurations for the class
     * data. It utilizes a cache to avoid repeated configuration lookups.
     *
     * @param classData The [ClassData] representing the class to be checked for instrumentation.
     * @return True if the class is instrumentable based on the available configurations; false otherwise.
     */
    override fun isInstrumentable(classData: ClassData): Boolean {
        var configs = cache[classData.className]
        if (configs == null && !cache.containsKey(classData.className)) {
            configs = getConfigForClassData(classData)
            cache[classData.className] = configs
        }
        return !configs.isNullOrEmpty()
    }

    /**
     * Retrieves the list of configurations that apply to the provided class data.
     *
     * This method filters the loaded configurations based on the package name, superclass,
     * interfaces, and class name of the given class data.
     *
     * @param classData The [ClassData] for which to retrieve applicable configurations.
     * @return A list of [Config] objects that match the criteria defined in the class data.
     */
    private fun getConfigForClassData(classData: ClassData): List<Config> = configs()
        .asSequence()
        .filter { config ->
            if (config.packageId.isAll()) return@filter true

            val configPackage = config.packageId.valueOrThrow()
            classData.className.startsWith("$configPackage.")
        }
        .filter { config ->
            if (config.superClass.isAll()) return@filter true

            val configSuperClass = config.superClass.valueOrThrow()
            classData.superClasses.contains(configSuperClass)
        }
        .filter { config ->
            if (config.interfaces.isAll()) return@filter true

            val configInterfaces = config.interfaces.valueOrThrow()
            classData.interfaces.intersect(configInterfaces.toSet()).isNotEmpty()
        }
        .filter { config ->
            if (config.clazz.isAll()) return@filter true

            val configClass = config.clazz.valueOrThrow()
            classData.className == configClass
        }
        .toList()


    /**
     * Loads and returns the list of configurations for method hooking.
     *
     * @return A list of [Config] objects loaded from the configuration files.
     */
    private fun configs(): List<Config> = synchronized(configs) {
        if (!isConfigLoaded) {
            isConfigLoaded = true
            configs = ConfigLoader().load(parameters.get().configs.get())
        }
        return configs
    }
}

private const val DEFAULT_LRU_SIZE = 25

/**
 * A simple LRU (Least Recently Used) cache implementation to store configuration
 * lists for classes. It removes the oldest entries when the maximum size is exceeded.
 *
 * @property maxSize The maximum number of entries allowed in the cache.
 */
private class LruCache<K, V>(private val maxSize: Int = DEFAULT_LRU_SIZE) : LinkedHashMap<K, V>() {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
        return size > maxSize
    }
}
