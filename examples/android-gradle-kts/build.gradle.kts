plugins {
    alias(libs.plugins.agp)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.methodhook)
}

android {
    namespace = "dev.aleksrychkov.example"
    compileSdk = 34

    defaultConfig {
        applicationId = "dev.aleksrychkov.example.android.kts"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
}

androidMethodHook {
    forceLogging = true
    forceClassTransform = true
    configs {
        create("debug") {
            addConfig("./methodhook/methodhook_activity.conf")
            addConfig("./methodhook/methodhook_fragment.conf")
            addConfig("./methodhook/methodhook_service.conf")
        }
    }
}
