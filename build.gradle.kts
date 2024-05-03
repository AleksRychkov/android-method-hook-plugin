import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.agp) apply false
    alias(libs.plugins.detekt)
}

subprojects {
    apply {
        plugin(rootProject.libs.plugins.detekt.get().pluginId)
    }

    detekt {
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
    }
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.layout.buildDirectory)
    dependsOn(gradle.includedBuild("methodhook").task(":clean"))
}

tasks.register("detektAll") {
    description = "Run detekt in all projects"

    dependsOn(":detekt")
    dependsOn(gradle.includedBuild("methodhook").task(":detekt"))
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        html.outputLocation.set(file("build/reports/detekt.html"))
    }
}
