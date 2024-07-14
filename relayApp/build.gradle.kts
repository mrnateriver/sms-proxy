import org.gradle.tooling.BuildException
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URI

plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.hilt)
    alias(libs.plugins.room)
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
    val basePackageName = "${rootProject.ext["basePackageName"]}.relay"
    namespace = basePackageName
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = basePackageName
        manifestPlaceholders["basePackageName"] = basePackageName

        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        fun validateNonEmpty(prop: String): String {
            val value =
                project.properties.get("${rootProject.ext["basePackageName"]}.$prop")?.toString()
            if (value.isNullOrBlank()) {
                throw RuntimeException("Project property '$prop' must be non-empty before build.")
            }
            return value
        }

        fun validateUrl(prop: String): String {
            val nonEmptyUrl = validateNonEmpty(prop)
            try {
                URI(nonEmptyUrl).toURL()
            } catch (e: Exception) {
                throw BuildException(
                    "Project property '$prop' must be set to a valid URL before build.",
                    e,
                )
            }
            return nonEmptyUrl
        }

        buildConfigField("String", "AUTHOR_WEB_PAGE_URL", "\"${validateUrl("authorWebPageUrl")}\"")
        buildConfigField("String", "API_BASE_URL", "\"${validateUrl("apiBaseUrl")}\"")
        buildConfigField("String", "API_KEY", "\"${validateNonEmpty("apiKey")}\"")
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
    room {
        schemaDirectory("$projectDir/schemas")
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
        implementation(libs.room.runtime)
        implementation(libs.room.ktx)

        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(composeBom)
        androidTestImplementation(libs.androidx.ui.test.junit4)
        debugImplementation(libs.androidx.ui.tooling)
        debugImplementation(libs.androidx.ui.test.manifest)
    }
}

ksp {
    arg("room.generateKotlin", "true")
}

dependencies {
    implementation(libs.android.hilt)
    ksp(libs.android.hilt.compiler)
    ksp(libs.room.compiler)
}
