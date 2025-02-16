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
    alias(libs.plugins.detekt)
    alias(libs.plugins.sentry.android)
    alias(libs.plugins.sentry.kotlin)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    sourceSets {
        androidMain.dependencies {}
    }
}

tasks {
    register<GenerateCertificatesTask>("generateProxyApiCertificate") {
        applicationName = "relayApp"
        outputPrivateKeyFile = "src/androidMain/assets/proxy-api-client-certificate-private-key.pem"
        outputCertificatesFiles = listOf(
            resolveProjectFilePath(
                "relayApp",
                "src/androidMain/assets/proxy-api-client-certificate.pem",
            ),
            resolveProjectFilePath(
                "server",
                "src/main/assets/proxy-api-relay-app.pem",
            ),
        )
    }
}

detekt {
    autoCorrect = false
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/detekt.yml")
    source.setFrom(
        "src/androidMain/kotlin",
        "src/androidInstrumentedTest/kotlin",
        "./build.gradle.kts",
    )
}

sentry {
    org = getProperty("sentry.org")
    projectName = getProperty("sentry.projectNamePrefix").let { "$it-relay" }
    authToken = System.getenv("SENTRY_AUTH_TOKEN") ?: getProperty("sentry.authToken")
    includeSourceContext = true
    telemetry = false

    autoInstallation {
        sentryVersion = "7.20.0"
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
        manifestPlaceholders["sentryDsn"] = System.getenv("SENTRY_DSN") ?: getProperty("sentry.dsn").orEmpty()

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
    signingConfigs {
        if (!System.getenv("RELEASE_STORE_PATH").isNullOrBlank()) {
            create("release") {
                storeFile = rootProject.file(System.getenv("RELEASE_STORE_PATH"))
                storePassword = System.getenv("RELEASE_STORE_PASSWORD")
                keyAlias = System.getenv("RELEASE_STORE_KEY_ALIAS")
                keyPassword = System.getenv("RELEASE_STORE_KEY_PASSWORD")
            }
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("release")
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
    installation {
        // This enables long timeouts required on slow environments, e.g. GitHub Actions
        timeOutInMs = 10 * 60 * 1000 // 10 minutes
        if (!System.getenv("CI").isNullOrBlank()) {
            installOptions.addAll(listOf("-d", "-t"))
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    room {
        schemaDirectory("$projectDir/schemas")
        generateKotlin = true
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
        implementation(libs.sentry.android)
        implementation(libs.sentry.compose)

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

ksp {
    arg("room.generateKotlin", "true")
}

dependencies {
    add("kspAndroid", libs.androidx.hilt.compiler)
    add("kspAndroid", libs.android.hilt.compiler)
    add("kspAndroid", libs.room.compiler)

    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.detekt.compose)
}
