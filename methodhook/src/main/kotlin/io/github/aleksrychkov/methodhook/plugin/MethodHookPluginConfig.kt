package io.github.aleksrychkov.methodhook.plugin

import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import javax.inject.Inject

/**
 * Configuration class for the MethodHook Gradle plugin.
 */
abstract class MethodHookPluginConfig @Inject constructor(
    objects: ObjectFactory
) {

    var forceLogging: Boolean = false
    var forceClassTransform: Boolean = false

    internal val configs = objects.domainObjectContainer(ConfigHandler::class.java)

    fun configs(action: Action<NamedDomainObjectContainer<ConfigHandler>>) {
        action.execute(configs)
    }

    abstract class ConfigHandler @Inject constructor(
        private val name: String,
        objects: ObjectFactory
    ) : Named {

        override fun getName(): String = name

        internal val configs: ListProperty<String> = objects.listProperty(String::class.java)

        fun addConfig(path: String) {
            this.configs.add(path)
        }
    }
}
