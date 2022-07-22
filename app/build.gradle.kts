plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.arsylk.androidex.app"
    compileSdk = 32

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.arsylk.androidex.app"
        minSdk = 24
        targetSdk = 32
        versionCode = 1
        versionName = "1.0.0"
    }



    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation(module(":lib"))
}