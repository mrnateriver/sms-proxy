import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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

tasks {
    register<GenerateCertificatesTask>("generateProxyApiCertificate") {
        applicationName = "relayApp"
        outputPrivateKeyFile = "src/androidMain/assets/proxy-api-client-certificate-private-key.pem"
        outputCertificatesFiles =
            listOf(
                resolveProjectFilePath(
                    "relayApp",
                    "src/androidMain/assets/proxy-api-client-certificate.pem"
                ),
                resolveProjectFilePath(
                    "server",
                    "src/main/resources/clients/proxy-api-relay-app.pem"
                )
            )
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

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        release {
            manifestPlaceholders["networkSecurityConfig"] = "@xml/network_security_config"
            isMinifyEnabled = false
        }
        debug {
            manifestPlaceholders["networkSecurityConfig"] = "@xml/network_security_config_debug"
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

        implementation(composeBom)
        implementation(libs.android.hilt)
        implementation(libs.androidx.hilt.work)
        implementation(libs.androidx.compat)
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.work.runtime.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(libs.androidx.datastore.preferences)
        implementation(libs.androidx.ui)
        implementation(libs.androidx.ui.graphics)
        implementation(libs.androidx.ui.tooling.preview)
        implementation(libs.androidx.material3)
        implementation(libs.androidx.navigation.compose)
        implementation(libs.google.accompanist)
        implementation(libs.room.runtime)
        implementation(libs.room.ktx)
        implementation(libs.androidx.hilt.navigation.compose)
        implementation(libs.androidx.lifecycle.runtime.compose.android)

        debugImplementation(libs.androidx.ui.tooling)
        debugImplementation(libs.androidx.ui.test.manifest)

        testImplementation(libs.junit)
        androidTestImplementation(composeBom)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.ui.test.junit4)
    }
}

ksp {
    arg("room.generateKotlin", "true")
}

dependencies {
    add("kspAndroid", libs.androidx.hilt.compiler)
    add("kspAndroid", libs.android.hilt.compiler)
    add("kspAndroid", libs.room.compiler)
}
