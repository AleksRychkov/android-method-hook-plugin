package io.github.aleksrychkov.methodhook.config

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigParseOptions
import java.io.File

/**
 * An interface for loading configuration settings.
 */
internal interface ConfigLoader {
    companion object {
        /**
         * Creates an instance of [ConfigLoader].
         *
         * @return A new instance of [ConfigLoader].
         */
        operator fun invoke(): ConfigLoader = ConfigLoaderImpl()
    }

    /**
     * Loads configurations from the specified list of files.
     *
     * @param files A list of configuration files to be loaded.
     * @return A list of [Config] instances parsed from the provided files.
     */
    fun load(files: List<File>): List<Config>
}

private class ConfigLoaderImpl : ConfigLoader {
    override fun load(files: List<File>): List<Config> {
        val result = mutableListOf<Config>()
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
