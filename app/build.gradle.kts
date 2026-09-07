import org.gradle.kotlin.dsl.getByName

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.digitalindonesia.fnb"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.digitalindonesia.fnb"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Tambahkan blok ini:
    signingConfigs {
        getByName("debug") {
            val userHome = System.getProperty("user.home")
            storeFile = file("$userHome/.android/debug.keystore")
        }
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
}

dependencies {
    // Menggunakan versi bawaan catalog (lebih aman & terpusat)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Library tambahan manual (karena belum ada di libs catalog)
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}