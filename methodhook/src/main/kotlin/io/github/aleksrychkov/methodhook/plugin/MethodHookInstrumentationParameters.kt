package io.github.aleksrychkov.methodhook.plugin

import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import java.io.File

/**
 * An interface defining parameters for method hooking instrumentation in the Android
 * build process.
 */
internal interface MethodHookInstrumentationParameters : InstrumentationParameters {

    /**
     * AGP will re-instrument dependencies, when the [InstrumentationParameters] changed
     * https://issuetracker.google.com/issues/190082518#comment4. This is just a dummy parameter
     * that is used solely for that purpose.
     */
    @get:Input
    @get:Optional
    val invalidate: Property<Long>

    @get:Input
    @get:Optional
    val configs: ListProperty<File>
}
