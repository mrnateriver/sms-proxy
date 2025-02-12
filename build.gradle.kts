extra["basePackageName"] = project.properties["basePackageName"]

plugins {
    alias(libs.plugins.sonarqube)

    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.aboutLibraries) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.openapi) apply false
    alias(libs.plugins.docker) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.detekt) apply false
}

sonar {
    properties {
        property("sonar.projectKey", getProperty("sonar.projectKey") ?: System.getenv("SONAR_PROJECT_KEY") ?: "server")
        property("sonar.organization", getProperty("sonar.org") ?: System.getenv("SONAR_ORGANIZATION") ?: "org")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

subprojects {
    configurations.all {
        resolutionStrategy.activateDependencyLocking()
    }
}

subprojects {
    configurations.all {
        resolutionStrategy.eachDependency {
            when (requested.module.toString()) {
                "com.google.protobuf:protobuf-java" -> {
                    when {
                        version.toString().startsWith("4.") -> useVersion("4.28.2")
                        else -> useVersion("3.25.5")
                    }
                }

                "io.netty:netty-codec-http2" -> {
                    useVersion("4.1.114.Final")
                }
            }
        }
    }
}
