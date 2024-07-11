import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm()

    sourceSets {
        androidMain.dependencies {
        }
    }
}

android {
    namespace = "io.mrnateriver.smsproxy.api"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    dependencies {
        implementation(libs.kotlin.reflect)
        implementation(libs.kotlinx.datetime)
        api(libs.moshi.kotlin)
        implementation(libs.moshi.adapters)
        implementation(libs.okhttp.loggingInterceptor)
        implementation(libs.retrofit.converter.moshi)
        implementation(libs.retrofit.converter.scalars)
        api(libs.retrofit)
    }
}
