afterEvaluate {
  subprojects.forEach { subproject ->
    val gitignore: File = subproject.projectDir.resolve(".gitignore")

    if (!gitignore.isFile) {
      throw GradleException("Subproject ${subproject.name} must have a .gitignore file")
    }
    val ignoredBuild = gitignore.readLines(Charsets.UTF_8).filter { it.contentEquals("build/") }

    if (ignoredBuild.isEmpty()) {
      throw GradleException("Subproject ${subproject.name} does not contain an entry 'build/'")
    }
  }
}
