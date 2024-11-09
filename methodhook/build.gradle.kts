@file:Suppress("UnstableApiUsage")

import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    alias(libs.plugins.detekt)
    alias(libs.plugins.plugin.publish)
}

group = "io.github.aleksrychkov"
version = "0.3"

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

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    languageVersion = "1.9"
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
            id = "io.github.aleksrychkov.methodhook"
            displayName = "Android method hook plugin"
            description =
                "An Android Gradle plugin to inject method call at the beginning and end of methods in Android application at compile time"
            tags.set(listOf("android", "android-gradle-plugin", "asm"))
            implementationClass = "io.github.aleksrychkov.methodhook.plugin.MethodHookPlugin"
        }
    }
}

gradlePlugin {
    website.set("https://github.com/aleksrychkov/android-method-hook-plugin")
    vcsUrl.set("https://github.com/aleksrychkov/android-method-hook-plugin.git")
}
