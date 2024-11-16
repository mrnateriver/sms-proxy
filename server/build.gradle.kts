plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.detekt)
    application
}

group = rootProject.ext["basePackageName"] as String
version = "1.0.0"
application {
    mainClass.set("$group.ApplicationKt")
    applicationDefaultJvmArgs =
        listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

tasks {
    register<GenerateCertificatesTask>("generateProxyApiCertificate") {
        applicationName = validateNonEmpty("serverCN")
        format = CertificateStorageFormat.JKS

        outputKeyStoreFile = "src/main/resources/proxy-api-server-certificate.jks"
        val apiServerPubKeyPath = "src/androidMain/assets/proxy-api-server-certificate.pem"
        outputCertificatesFiles = listOf(
            resolveProjectFilePath("relayApp", apiServerPubKeyPath),
            resolveProjectFilePath("receiverApp", apiServerPubKeyPath),
        )
    }
}

detekt {
    autoCorrect = false
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/detekt.yml")
    source.setFrom(
        "src/main/kotlin",
        "src/test/kotlin",
        "./build.gradle.kts",
    )
}

dependencies {
    implementation(projects.shared)

    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.sqldelight.driver.jdbc)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.adapters)
    implementation(libs.jetty.http)
    implementation(libs.jetty.util)

    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)

    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.detekt.compose)
}

sqldelight {
    databases {
        create("MessagesDatabase") {
            packageName.set("${rootProject.ext.get("basePackageName")}.shared.db")
            dialect(libs.sqldelight.dialect.postgresql)
        }
    }
}
