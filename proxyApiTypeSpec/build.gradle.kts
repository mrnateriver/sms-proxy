import com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStartContainer
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask as OpenApiGenerateTask

plugins {
    alias(libs.plugins.openapi)
    alias(libs.plugins.docker)
}

val rootGroupId = rootProject.ext["basePackageName"] as String
val rootPackage = rootGroupId
val proxyApiSpecDir = layout.projectDirectory
val proxyApiSpecPath =
    proxyApiSpecDir.file("tsp-output/@typespec/openapi3/openapi.yaml")

val buildTypeSpecApiGenImageName = "buildTypeSpecApiGenImage"
val typeSpecApiGenContainerTag = "${rootGroupId.replace('.', '-')}-api-gen:latest"

tasks {
    register<DockerBuildImage>(buildTypeSpecApiGenImageName) {
        inputDir = proxyApiSpecDir
        images.add(typeSpecApiGenContainerTag)
    }

    val createTypeSpecApiGenContainer =
        register<DockerCreateContainer>("createTypeSpecApiGenContainer") {
            dependsOn(buildTypeSpecApiGenImageName)

            imageId = typeSpecApiGenContainerTag
            workingDir = "/app"
            hostConfig.run {
                autoRemove = true
                binds.put("$proxyApiSpecDir/tsp-output", "/app/tsp-output")
            }
        }

    val startTypeSpecApiGenContainer =
        register<DockerStartContainer>("startTypeSpecApiGenContainer") {
            dependsOn(createTypeSpecApiGenContainer)
            containerId = createTypeSpecApiGenContainer.get().containerId
        }

    val clearServerDirectory = register<Delete>("clearApiServerOutputDirectory") {
        clearCodegenOutput("proxyApiServer")
    }

    register<OpenApiGenerateTask>("generateApiServer") {
        dependsOn(listOf(startTypeSpecApiGenContainer, clearServerDirectory))

        // FIXME: not workable yet
        generatorName = "kotlin-server"
        library = "jaxrs-spec"

        configureCommon("proxyApiServer")
    }

    register<Delete>("clearApiClientOutputDirectory") {
        clearCodegenOutput("proxyApiClient")
    }

    register<OpenApiGenerateTask>("generateApiClient") {
        dependsOn(listOf(startTypeSpecApiGenContainer, clearServerDirectory))

        generatorName = "kotlin"
        library = "jvm-retrofit2"

        configureCommon("proxyApiClient", "src/androidMain/kotlin")
    }

    register("generateApi") {
        dependsOn("generateApiServer", "generateApiClient")
    }
}

private fun OpenApiGenerateTask.configureCommon(
    outputDirSuffix: String,
    sourceFolder: String = "src/main/kotlin",
) {
    inputSpec = proxyApiSpecPath.asFile.absolutePath

    outputDir = rootProject.layout.projectDirectory.dir(outputDirSuffix).asFile.absolutePath
    ignoreFileOverride = "${outputDir.get()}/.openapi-generator-ignore"
    groupId = rootGroupId
    packageName = rootPackage
    apiPackage = "$rootPackage.api"
    modelPackage = "$rootPackage.models"

    globalProperties = mapOf(
        "modelDocs" to "false",
    )
    configOptions = mapOf(
        "idea" to "true",
        "sourceFolder" to sourceFolder,
        "groupId" to rootGroupId,
        "dateLibrary" to "kotlinx-datetime",
        "interfaceOnly" to "true",
        "omitGradleWrapper" to "true",
        "omitGradlePluginVersions" to "true",
        "generateOneOfAnyOfWrappers" to "true",
        "useSettingsGradle" to "false",
        "useCoroutines" to "true",
    )

    cleanupOutput = false
    skipValidateSpec = false
    logToStderr = true
    generateAliasAsModel = false
    enablePostProcessFile = false
    generateApiTests = false
    generateApiDocumentation = false
    generateModelTests = false
    generateModelDocumentation = false
}

private fun Delete.clearCodegenOutput(outputDir: String) {
    val rootDir = rootProject.layout.projectDirectory
    delete(fileTree(rootDir.dir(outputDir).dir("src")) { include("**/*") })
}

