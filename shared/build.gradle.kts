import com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStartContainer
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask as OpenApiGenerateTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.openapi)
    alias(libs.plugins.docker)
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
        }
        commonMain.dependencies {
            api(libs.kotlinx.datetime)
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

val rootPackage = "io.mrnateriver.smsproxy.shared"
val proxyApiSpecDir = layout.projectDirectory.dir("src/commonMain/typespec/proxy-api")
val proxyApiSpecPath = proxyApiSpecDir.file("tsp-output/@typespec/openapi3/openapi.yaml")
val proxyApiOutputDirBase = layout.buildDirectory.dir("generated")

val buildTypeSpecApiGenImageName = "buildTypeSpecApiGenImage"
val typeSpecApiGenContainerTag = "sms-proxy-api-gen:latest"
tasks.register<DockerBuildImage>(buildTypeSpecApiGenImageName) {
    inputDir = proxyApiSpecDir
    images.add(typeSpecApiGenContainerTag)
}

val createTypeSpecApiGenContainerName = "createTypeSpecApiGenContainer"
tasks.register<DockerCreateContainer>(createTypeSpecApiGenContainerName) {
    dependsOn(buildTypeSpecApiGenImageName)

    imageId = typeSpecApiGenContainerTag
    workingDir = "/app"
    hostConfig.run {
        autoRemove = true
        binds.put("$proxyApiSpecDir/tsp-output", "/app/tsp-output")
    }
}

tasks.register<DockerStartContainer>("startTypeSpecApiGenContainer") {
    dependsOn(createTypeSpecApiGenContainerName)

    containerId =
        tasks.getByName<DockerCreateContainer>(createTypeSpecApiGenContainerName).containerId
}

apply(from = "src/commonMain/typespec/proxy-api/build.gradle.kts")

// TODO: this generates buildable modules; we need either to incorporate those modules into the whole repo, or refactor the codegen to generate source files directly

fun OpenApiGenerateTask.configureCommon(outputDirSuffix: String) {
    inputSpec = proxyApiSpecPath.asFile.absolutePath

    outputDir = "${proxyApiOutputDirBase}/$outputDirSuffix"
    packageName = rootPackage
    apiPackage = "$rootPackage.api"
    modelPackage = "$rootPackage.models"

    configOptions = mapOf("dateLibrary" to "kotlinx-datetime", "interfaceOnly" to "true")
    skipValidateSpec = false
    logToStderr = true
    generateAliasAsModel = false
    enablePostProcessFile = false
}

tasks.register<OpenApiGenerateTask>("generateApiServer") {
    generatorName = "kotlin-server"
    library = "ktor"

    configureCommon("api-server")
}

tasks.register<OpenApiGenerateTask>("generateApiClient") {
    generatorName = "kotlin"
    library = "jvm-retrofit2"

    configureCommon("api-client")
}

tasks.register("generateApi") {
    dependsOn("generateApiServer", "generateApiClient")
}

android {
    namespace = rootPackage
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    dependencies {
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.material3)
        implementation(libs.androidx.ui.text.google.fonts)
    }
}
