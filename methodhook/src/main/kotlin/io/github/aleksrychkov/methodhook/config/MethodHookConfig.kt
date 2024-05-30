package io.github.aleksrychkov.methodhook.config

import java.io.Serializable

data class MethodHookConfig(
    val superClass: String,
    val exactClass: String,
    val methods: Set<Method>,
    val beginMethodWith: String,
    val endMethodWith: String,
    val packageId: String,
) : Serializable {

    companion object {
        @Suppress("ConstPropertyName")
        private const val serialVersionUID: Long = 1

        const val CONF_SUPER_CLASS = "superClass"
        const val CONF_EXACT_CLASS = "exactClass"
        const val CONF_METHODS = "methods"
        const val CONF_BEGIN_METHOD_WITH = "beginMethodWith"
        const val CONF_END_METHOD_WITH = "endMethodWith"
        const val CONF_PACKAGE_ID = "packageId"
    }

    fun hasMethod(name: String): Boolean =
        methods.find { it.name == name } != null

    override fun toString(): String {
        return """
MethodHookConfig 
    $CONF_SUPER_CLASS='$superClass'
    $CONF_EXACT_CLASS='$exactClass'
    $CONF_METHODS=$methods
    $CONF_BEGIN_METHOD_WITH='$beginMethodWith'
    $CONF_END_METHOD_WITH='$endMethodWith'
    $CONF_PACKAGE_ID='$packageId'            
        """.trimMargin()
    }

    data class Method(val name: String) : Serializable {

        companion object {
            @Suppress("ConstPropertyName")
            private const val serialVersionUID: Long = 1
        }
    }
}
