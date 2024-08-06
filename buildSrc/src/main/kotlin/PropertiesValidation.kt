import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import org.gradle.tooling.BuildException
import java.net.URI

fun Project.validateNonEmpty(prop: String): String {
    val value = project.properties.get("${rootProject.extra["basePackageName"]}.$prop")?.toString()
    if (value.isNullOrBlank()) {
        throw RuntimeException("Project property '$prop' must be non-empty before build.")
    }
    return value
}

fun Project.validateUrl(prop: String): String {
    val nonEmptyUrl = validateNonEmpty(prop)
    try {
        URI(nonEmptyUrl).toURL()
    } catch (e: Exception) {
        throw BuildException(
            "Project property '$prop' must be set to a valid URL before build.",
            e,
        )
    }
    return nonEmptyUrl
}
