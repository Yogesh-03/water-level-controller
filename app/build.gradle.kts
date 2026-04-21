import org.gradle.kotlin.dsl.implementation

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.waterlevelcontroller"
    compileSdk = 36


    defaultConfig {
        applicationId = "com.example.waterlevelcontroller"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    kapt{
        correctErrorTypes = true
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

    composeOptions {
     //   kotlinCompilerExtensionVersion = "1.5.8"
    }

    kotlinOptions{
        jvmTarget = "17"
    }
    kotlin {
        jvmToolchain(17)
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation(libs.androidx.room.runtime.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    //Jetpack Compose
    //debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")

    //Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    //Hilt Dependency Injection

    //ksp("com.google.dagger:hilt-compiler:2.50")

// Hilt + ViewModel + Compose
  //  implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // ✅ HILT
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")

// ✅ Hilt + Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Coroutine + Flow
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Firebase Coroutine support
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    //implementation("androidx.compose:compose-bom:2024.06.00")
}


