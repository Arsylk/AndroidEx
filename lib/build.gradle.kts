plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.arsylk.androidex.lib"
    compileSdk = 32

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        minSdk = 19
        targetSdk = 32

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
    }
    lint {
        checkDependencies = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    api("androidx.recyclerview:recyclerview:1.2.1")

    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0")
    api("androidx.lifecycle:lifecycle-runtime-ktx:2.5.0")

    implementation(project(":lint"))
    lintPublish(project(":lint"))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}