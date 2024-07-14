extra["basePackageName"] = project.properties["basePackageName"]

plugins {
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
}