plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    dependencyLocking {
        lockAllConfigurations()
    }

    compileOnly(libs.kotlin.gradle.plugin)
    implementation(libs.ktor.network.tls.certificates)
    implementation(libs.bcpkix.jdk18on)
}
