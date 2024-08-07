plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.kotlin.gradle.plugin)
    implementation(libs.ktor.network.tls.certificates)
    implementation(libs.bcpkix.jdk18on)
}
