package dev.aleksrychkov.methodhook.config

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigParseOptions
import dev.aleksrychkov.methodhook.utils.Log
import java.io.File

interface MethodHookConfigLoader {
    fun loadConfigs(configFiles: List<File>): List<MethodHookConfig>
}

private class MethodHookConfigLoaderImpl : MethodHookConfigLoader {

    override fun loadConfigs(configFiles: List<File>): List<MethodHookConfig> {
        val configs = mutableListOf<MethodHookConfig>()

        configFiles.forEach { file ->
            if (file.exists()) {
                val config = ConfigFactory
                    .parseFile(file, ConfigParseOptions.defaults().setAllowMissing(false))
                    .resolve()
                config.root().keys
                    .forEach { key ->
                        configs.add(MethodHookConfigMapper.map(config.getConfig(key)))
                    }
            }
        }

        println()
        Log.i("Loaded next configs:\n${configs.joinToString("\n")}")
        println()

        return configs
    }
}

fun MethodHookConfigLoader(): MethodHookConfigLoader = MethodHookConfigLoaderImpl()
