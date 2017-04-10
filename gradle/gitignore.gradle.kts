afterEvaluate {
  subprojects.forEach { subproject ->
    val gitignore: File = subproject.projectDir.resolve(".gitignore")

    if (!gitignore.isFile) {
      throw GradleException("Subproject ${subproject.name} must have a .gitignore file")
    }
    val ignoresBuildDir = gitignore.readLines(Charsets.UTF_8).any { it.contentEquals("build/") }

    if (!ignoresBuildDir) {
      throw GradleException("Subproject ${subproject.name} does not contain an entry 'build/'")
    }
  }
}
