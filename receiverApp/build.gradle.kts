import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.detekt)
    alias(libs.plugins.sentry)
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
        androidMain.dependencies {
        }
        commonMain.dependencies {
            implementation(projects.shared)
        }
    }
}

tasks {
    register<GenerateCertificatesTask>("generateProxyApiCertificate") {
        applicationName = "receiverApp"
        outputPrivateKeyFile = "src/androidMain/assets/proxy-api-client-certificate-private-key.pem"
        outputCertificatesFiles =
            listOf(
                resolveProjectFilePath(
                    "receiverApp",
                    "src/androidMain/assets/proxy-api-client-certificate.pem",
                ),
                resolveProjectFilePath(
                    "server",
                    "src/main/resources/clients/proxy-api-receiver-app.pem",
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
    projectName = getProperty("sentry.projectNamePrefix").let { "$it-receiver" }
    authToken = System.getenv("SENTRY_AUTH_TOKEN") ?: getProperty("sentry.authToken")
    includeSourceContext = true
    telemetry = false
}

android {
    val basePackageName = "${rootProject.ext["basePackageName"]}.receiver"
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
        getByName("release") {
            signingConfig = signingConfigs.findByName("release")
            isMinifyEnabled = false
        }
    }
    compileOptions {
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
    }
    dependencies {
        val composeBom = platform(libs.androidx.compose.bom)

        implementation(libs.androidx.compat)
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(composeBom)
        implementation(libs.androidx.ui)
        implementation(libs.androidx.ui.graphics)
        implementation(libs.androidx.ui.tooling.preview)
        implementation(libs.androidx.material3)

        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(composeBom)
        androidTestImplementation(libs.androidx.ui.test.junit4)
        debugImplementation(libs.androidx.ui.tooling)
        debugImplementation(libs.androidx.ui.test.manifest)
    }
}

dependencies {
    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.detekt.compose)
}
