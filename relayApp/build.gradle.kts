import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("kotlin-kapt")

    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.hilt)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    sourceSets {
        androidMain.dependencies {
        }
    }
}

android {
    namespace = "io.mrnateriver.smsproxy.relay"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "io.mrnateriver.smsproxy.relay"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        buildConfigField(
            "String",
            "AUTHOR_WEB_PAGE_URL",
            "\"${System.getenv("AUTHOR_WEB_PAGE_URL") ?: "https://mrnateriver.io"}\""
        )
        buildConfigField(
            "String",
            "API_BASE_URL",
            "\"${System.getenv("API_BASE_URL") ?: "https://localhost:3000"}\""
        )
        // TODO: show a human-readable error message in UI if the API key is not set
        buildConfigField(
            "String",
            "API_KEY",
            "\"${System.getenv("API_KEY") ?: ""}\""
        )
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    dependencies {
        coreLibraryDesugaring(libs.android.desugar.jdk.libs)

        val composeBom = platform(libs.androidx.compose.bom)

        implementation(projects.shared)
        implementation(projects.proxyApiClient)

        implementation(libs.androidx.compat)
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(composeBom)
        implementation(libs.androidx.ui)
        implementation(libs.androidx.ui.graphics)
        implementation(libs.androidx.ui.tooling.preview)
        implementation(libs.androidx.material3)
        implementation(libs.androidx.navigation.compose)
        implementation(libs.google.accompanist)
        implementation(libs.about.libraries)
        implementation(libs.compose.preferences)

        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(composeBom)
        androidTestImplementation(libs.androidx.ui.test.junit4)
        debugImplementation(libs.androidx.ui.tooling)
        debugImplementation(libs.androidx.ui.test.manifest)
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(libs.android.hilt)
    "kapt"(libs.android.hilt.compiler)
}
