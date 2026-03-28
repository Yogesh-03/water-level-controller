plugins {
    alias(libs.plugins.android.application)

    //id("org.jetbrains.kotlin.android")   // ✅ REQUIRED

    //alias(libs.plugins.kotlin.compose)
    //id("org.jetbrains.kotlin.kapt")      // ✅ FIX kapt issue
   // id("com.google.devtools.ksp")

    //id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    //id("com.google.gms.google-services")
    //alias(libs.plugins.ksp)
    //id("dagger.hilt.android.plugin")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    //Jetpack Compose
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")

    //Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    //Hilt Dependency Injection

    //ksp("com.google.dagger:hilt-compiler:2.50")

// Hilt + ViewModel + Compose
  //  implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // ✅ HILT
    implementation("com.google.dagger:hilt-android:2.50")
    implementation("com.google.dagger:hilt-compiler:2.50")

// ✅ Hilt + Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
}

fun kapt(configure: String) {}
