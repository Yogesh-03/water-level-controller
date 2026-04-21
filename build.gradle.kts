    // Top-level build file where you can add configuration options common to all sub-projects/modules.
    buildscript {
        dependencies {
            classpath("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
        }
    }

//    allprojects {
//        configurations.all {
//            resolutionStrategy {
//                force("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
//                force("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
//                force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22")
//            }
//        }
//    }


    plugins {
        id("com.android.application") version "8.5.2" apply false
        id("org.jetbrains.kotlin.android") version "2.1.20" apply false
        id("com.google.dagger.hilt.android") version "2.51.1" apply false
        id("com.google.gms.google-services") version "4.4.1" apply false

    } //id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
        //id("com.google.dagger.hilt.android") version "2.50" apply false