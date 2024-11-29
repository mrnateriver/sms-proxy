plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ktor)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.detekt)
    application
}

group = "${rootProject.ext["basePackageName"]}.server"
version = "1.0.0"
application {
    mainClass.set("$group.ApplicationKt")

    val apiKey = validateNonEmpty("apiKey")

    applicationDefaultJvmArgs =
        listOf(
            "-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}",
            "-D$group.apiKey=$apiKey",
        )
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
    implementation(projects.proxyApiServer)

    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.server.serializationKotlinxJson)
    implementation(libs.ktor.server.statusPages)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.sqldelight.driver.jdbc)
    implementation(libs.postgresql.jdbc)
    implementation(libs.hikari)
    implementation(libs.dagger)

    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)

    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.detekt.compose)

    ksp(libs.dagger.compiler)
}

sqldelight {
    databases {
        create("Database") {
            dialect(libs.sqldelight.dialect.postgresql)
            packageName.set("$group.db")

            deriveSchemaFromMigrations = true

            migrationOutputDirectory = layout.buildDirectory.dir("resources/main/migrations")
            migrationOutputFileFormat = ".sql"
        }
    }
}

tasks {
    compileKotlin.configure {
        dependsOn("generateMainDatabaseMigrations")
    }
}
