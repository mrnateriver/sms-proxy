import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import java.io.FileInputStream
import java.util.Properties

fun Project.getProperty(prop: String): String? {
    val key = "${rootProject.extra["basePackageName"]}.$prop"
    return getLocalProperty(key) ?: project.properties.get(key)?.toString()
}

val localProperties = Properties()
private fun Project.getLocalProperty(prop: String): String? {
    // Hopefully local.properties will at least contain the local SDK path so an empty property bag is an indicator that it hasn't been loaded yet
    if (localProperties.size == 0) {
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            FileInputStream(localPropertiesFile).use { stream ->
                localProperties.load(stream)
            }
        }
    }

    if (!localProperties.containsKey(prop)) {
        return null
    }

    return localProperties.get(prop)?.toString()
}
