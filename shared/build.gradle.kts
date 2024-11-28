import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.detekt)
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
            api(projects.proxyApiClient)
            api(libs.compose.preferences)

            implementation(libs.about.libraries)
            implementation(libs.okhttp.loggingInterceptor)
            implementation(libs.okhttp.tls)

            compileOnly(libs.sentry.android)
        }
        jvmMain.dependencies {
        }
        commonMain.dependencies {
            api(libs.kotlinx.datetime)
            api(libs.arrow.core)

            implementation(libs.kotlinx.coroutines)

            // We need Compose for the androidMain source set, but we can't apply the compose compiler plugin
            // only to a specific source set; as a result, we need to include the runtime for effectively all
            // source sets
            compileOnly(libs.androidx.ui.runtime)
        }
        commonTest.dependencies {
            implementation(libs.junit)
            implementation(libs.kotlin.test)
            implementation(libs.kotlin.test.junit)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.mockito.kotlin)
        }
    }
}

detekt {
    autoCorrect = false
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/detekt.yml")
    source.setFrom(
        "src/androidMain/kotlin",
        "src/androidInstrumentedTest/kotlin",
        "src/commonMain/kotlin",
        "src/commonTest/kotlin",
        "./build.gradle.kts",
    )
}

android {
    namespace = "${rootProject.ext["basePackageName"]}.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()

        buildConfigField("long", "API_TIMEOUT_MS", "${validateNonEmpty("apiTimeoutMs")}L")
        buildConfigField("String", "API_BASE_URL", "\"${validateUrl("apiBaseUrl")}\"")
        buildConfigField("String", "API_KEY", "\"${validateNonEmpty("apiKey")}\"")
        buildConfigField("String", "AUTHOR_WEB_PAGE_URL", "\"${validateUrl("authorWebPageUrl")}\"")
        buildConfigField("String", "API_SERVER_CN", "\"${validateNonEmpty("serverCN")}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    dependencies {
        val composeBom = platform(libs.androidx.compose.bom)

        implementation(composeBom)

        // Compose BOM is not supported in KMP, so the dependencies are declared in android {} block
        implementation(libs.androidx.material3)
        implementation(libs.androidx.ui.text.google.fonts)
        implementation(libs.androidx.ui.tooling.preview)
        implementation(libs.androidx.navigation.compose)

        debugImplementation(libs.androidx.ui.tooling)
        debugImplementation(libs.androidx.ui.test.manifest)

        testImplementation(libs.junit)
        androidTestImplementation(composeBom)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.ui.test.junit4)
        androidTestImplementation(libs.mockito.kotlin)
        androidTestImplementation(libs.mockito.android)
    }
}

dependencies {
    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.detekt.compose)
}
