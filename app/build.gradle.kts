import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
    alias(libs.plugins.hilt)
    kotlin("plugin.serialization") version "2.0.21"
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}
val kakaoNativeKey: String = localProperties.getProperty("NATIVE_KEY") ?: ""

android {
    namespace = "com.team22.soundary"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.team22.soundary"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "NATIVE_KEY", "\"$kakaoNativeKey\"")

        manifestPlaceholders["NATIVE_KEY"] = kakaoNativeKey
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {

        enable = true
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.androidx.cardview)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.gridlayout)
    implementation(libs.hilt.android)
    implementation(libs.androidx.media3.exoplayer)
    kapt(libs.hilt.compiler)
    implementation(libs.glide)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.retrofit.serialization)
    implementation(libs.kakao.sdk)
    implementation(libs.kotlinx.serialization)
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    
}
