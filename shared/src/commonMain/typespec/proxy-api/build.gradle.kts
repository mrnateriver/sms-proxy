import com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStartContainer
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage

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

