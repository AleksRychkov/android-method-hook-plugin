package dev.aleksrychkov.methodhook.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import dev.aleksrychkov.methodhook.asm.MethodHookClassVisitor
import dev.aleksrychkov.methodhook.config.MethodHookConfig
import dev.aleksrychkov.methodhook.config.MethodHookConfigLoader
import dev.aleksrychkov.methodhook.config.MethodHookConfigValidator
import dev.aleksrychkov.methodhook.utils.MethodHookInstrumentableHelper.findInstrumentable
import org.objectweb.asm.ClassVisitor
import java.util.Collections

abstract class MethodHookAsmClassVisitorFactory :
    AsmClassVisitorFactory<MethodHookInstrumentationParameters> {

    companion object {
        private val cache = Collections.synchronizedMap(HashMap<String, MethodHookConfig?>())
        private var configs: MutableList<MethodHookConfig> = mutableListOf()
        private var isConfigLoaded = false
    }

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor,
    ): ClassVisitor {
        val config = cache[classContext.currentClassData.className]
        return if (config != null) {
            MethodHookClassVisitor(
                api = instrumentationContext.apiVersion.get(),
                cv = nextClassVisitor,
                config = config,
            )
        } else {
            nextClassVisitor
        }
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        var config = cache[classData.className]
        if (config == null && !cache.containsKey(classData.className)) {
            config = configs().findInstrumentable(classData)
            cache[classData.className] = config
        }

        return config != null
    }

    private fun configs(): List<MethodHookConfig> = synchronized(configs) {
        if (!isConfigLoaded) {
            isConfigLoaded = true
            configs.addAll(MethodHookConfigLoader().loadConfigs(parameters.get().configs.get()))
            MethodHookConfigValidator().validate(configs)
        }
        return configs
    }
}
