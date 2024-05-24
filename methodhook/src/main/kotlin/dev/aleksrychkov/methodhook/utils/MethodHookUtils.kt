package dev.aleksrychkov.methodhook.utils

fun String.convertDescriptor(): String {
    return runCatching { this.internalConvertDescriptor() }.getOrNull() ?: ""
}

private val typeMap by lazy {
    mapOf(
        'B' to "byte",
        'C' to "char",
        'D' to "double",
        'F' to "float",
        'I' to "int",
        'J' to "long",
        'S' to "short",
        'Z' to "boolean",
        'V' to "void",
    )
}

private fun String.internalConvertDescriptor(): String {
    val descriptor = this
    val params = mutableListOf<String>()
    var index = 0

    fun parseType(): String? {
        return when (val c = descriptor[index]) {
            in typeMap.keys -> {
                index++
                typeMap[c]
            }

            'L' -> {
                val semiColonIndex = descriptor.indexOf(';', index)
                val className = descriptor
                    .substring(index, semiColonIndex)
                    .replace('/', '.')
                    .substringAfterLast(".")
                index = semiColonIndex + 1
                className
            }

            '[' -> {
                index++
                parseType() + "[]"
            }

            else -> {
                index++
                null
            }
        }
    }

    while (index < descriptor.length) {
        parseType()?.let(params::add)
    }

    val returnType = params.removeLast()
    return "(${params.joinToString(",")})->$returnType"
}
