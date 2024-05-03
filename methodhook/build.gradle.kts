import io.gitlab.arturbosch.detekt.Detekt

plugins {
    `kotlin-dsl`
    alias(libs.plugins.detekt)
    `maven-publish`
}

group = "dev.aleksrychkov.methodhook"
version = "0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    compileOnly(libs.agp)
    compileOnly(libs.agp.api)

    implementation(libs.bundles.asm)
    implementation(libs.typesafe)

    testImplementation(libs.juint)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        setEvents(setOf("passed", "skipped", "failed"))
        showStandardStreams = true
    }
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(rootProject.files("../config/detekt/detekt.yml"))
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        html.outputLocation.set(file("build/reports/detekt.html"))
    }
}

gradlePlugin {
    plugins {
        create("methodhook-plugin") {
            id = "dev.aleksrychkov.methodhook"
            displayName = "Android method hook plugin"
            description =
                "An Android Gradle plugin to inject method call at the beginning and end of methods in Android application at compile time"
            @Suppress("UnstableApiUsage")
            tags.addAll(listOf("android", "bytecode", "weaver", "asm"))
            implementationClass = "dev.aleksrychkov.methodhook.plugin.MethodHookPlugin"
        }
    }
}
