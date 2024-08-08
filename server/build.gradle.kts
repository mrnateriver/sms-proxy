plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.sqldelight)
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
        applicationName = "proxyApi"
        format = CertificateStorageFormat.JKS

        val apiServerPubKeyPath = "src/androidMain/assets/proxy-api-server.pubkey"
        outputPublicKeySha256Files = listOf(
            resolveProjectFilePath("relayApp", apiServerPubKeyPath),
            resolveProjectFilePath("receiverApp", apiServerPubKeyPath),
        )
    }
}

dependencies {
    implementation(projects.shared)

    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.sqldelight.driver.jdbc)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.adapters)

    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
}

sqldelight {
    databases {
        create("MessagesDatabase") {
            packageName.set("${rootProject.ext.get("basePackageName")}.shared.db")
            dialect(libs.sqldelight.dialect.postgresql)
        }
    }
}
