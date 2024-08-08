import org.gradle.api.Project

fun Project.resolveProjectFilePath(projectName: String, path: String): String {
    return rootProject.childProjects.get(projectName)?.file(path)?.absolutePath
        ?: throw IllegalArgumentException("Project $projectName not found")
}