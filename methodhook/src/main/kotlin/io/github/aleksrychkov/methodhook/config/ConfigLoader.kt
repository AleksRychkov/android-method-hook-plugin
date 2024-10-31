package io.github.aleksrychkov.methodhook.config

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigParseOptions
import java.io.File

internal interface ConfigLoader {
    companion object {
        operator fun invoke(): ConfigLoader {
            return ConfigLoaderImpl()
        }
    }

    fun load(files: List<File>): List<MethodHookConfig>
}

private class ConfigLoaderImpl : ConfigLoader {
    override fun load(files: List<File>): List<MethodHookConfig> {
        val result = mutableListOf<MethodHookConfig>()
        files
            .filter { it.exists() }
            .forEach { file ->
                val config = ConfigFactory
                    .parseFile(file, ConfigParseOptions.defaults().setAllowMissing(false))
                    .resolve()

                val mapper = ConfigMapper()
                config.root().keys
                    .map { path -> config.getConfig(path) }
                    .map(mapper::map)
                    .let(result::addAll)
            }
        return result
    }
}
