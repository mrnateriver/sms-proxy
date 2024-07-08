import com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStartContainer
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask as OpenApiGenerateTask

plugins {
    alias(libs.plugins.openapi)
    alias(libs.plugins.docker)
}

val rootPackage = "io.mrnateriver.smsproxy.shared"
val proxyApiSpecDir = layout.projectDirectory
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
