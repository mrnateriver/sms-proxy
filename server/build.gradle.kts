plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ktor)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.detekt)
    alias(libs.plugins.sentry.jvm)
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
        val apiServerPubKeyPath = "src/androidMain/assets/proxy-api-server-certificate.pem"

        applicationName = validateNonEmpty("serverCN")

        outputKeyStoreFile = "src/main/assets/server.jks"
        outputPrivateKeyFile = "src/main/assets/server-private-key.pem"
        outputCertificatesFiles = listOf(
            resolveProjectFilePath("server", "src/main/assets/server.pem"),
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

sentry {
    org = getProperty("sentry.org")
    projectName = getProperty("sentry.projectNamePrefix").let { "$it-server" }
    authToken = System.getenv("SENTRY_AUTH_TOKEN") ?: getProperty("sentry.authToken")
    includeSourceContext = true
    telemetry = false
}

tasks.named("generateSentryBundleIdJava") {
    dependsOn("generateMainDatabaseInterface", "kspKotlin")
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
    implementation(libs.ktor.server.metrics.micrometer)
    implementation(libs.micrometer.registry.prometheus)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.sqldelight.driver.jdbc)
    implementation(libs.postgresql.jdbc)
    implementation(libs.hikari)
    implementation(libs.dagger)
    implementation(libs.opentelemetry.sdk)
    implementation(libs.opentelemetry.exporter.logging)
    implementation(libs.opentelemetry.exporter.otlp)
    implementation(libs.opentelemetry.exporter.prometheus)
    implementation(libs.opentelemetry.instrumentation.ktor)
    implementation(libs.opentelemetry.instrumentation.logbackMdc)
    implementation(libs.opentelemetry.instrumentation.micrometer)
    implementation(libs.opentelemetry.extension.kotlin)
    implementation(libs.opentelemetry.semconv.incubating)

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
