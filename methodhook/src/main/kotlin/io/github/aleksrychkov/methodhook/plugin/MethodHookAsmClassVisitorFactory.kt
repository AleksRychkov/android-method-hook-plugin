package io.github.aleksrychkov.methodhook.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import io.github.aleksrychkov.methodhook.asm.InjectorClassVisitor
import io.github.aleksrychkov.methodhook.config.ConfigLoader
import io.github.aleksrychkov.methodhook.config.MethodHookConfig
import io.github.aleksrychkov.methodhook.config.isAll
import io.github.aleksrychkov.methodhook.config.valueOrThrow
import org.objectweb.asm.ClassVisitor
import java.util.Collections

internal abstract class MethodHookAsmClassVisitorFactory :
    AsmClassVisitorFactory<MethodHookInstrumentationParameters> {

    companion object {
        private var cache = Collections.synchronizedMap(LruCache<String, List<MethodHookConfig>?>())
        private var configs: List<MethodHookConfig> = emptyList()
        private var isConfigLoaded = false
    }

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

    override fun isInstrumentable(classData: ClassData): Boolean {
        var configs = cache[classData.className]
        if (configs == null && !cache.containsKey(classData.className)) {
            configs = getConfigForClassData(classData)
            cache[classData.className] = configs
        }
        return !configs.isNullOrEmpty()
    }

    private fun getConfigForClassData(classData: ClassData): List<MethodHookConfig> = configs()
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


    private fun configs(): List<MethodHookConfig> = synchronized(configs) {
        if (!isConfigLoaded) {
            isConfigLoaded = true
            configs = ConfigLoader().load(parameters.get().configs.get())
        }
        return configs
    }
}

private const val DEFAULT_LRU_SIZE = 25

private class LruCache<K, V>(private val maxSize: Int = DEFAULT_LRU_SIZE) : LinkedHashMap<K, V>() {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
        return size > maxSize
    }
}
