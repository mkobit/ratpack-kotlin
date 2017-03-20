buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath("org.junit.platform:junit-platform-gradle-plugin:1.0.0-M3")
  }
}

plugins {
  id("com.gradle.build-scan") version "1.6"
  id("org.jetbrains.kotlin.jvm") version "1.1.1" apply false
  id("com.github.ben-manes.versions") version "0.14.0"
}

allprojects {
  group = "com.mkobit.ratpack"
  version = "0.1.0"

  repositories {
    jcenter()
  }
}

fun env(key: String): String? = System.getenv(key)

buildScan {
  setLicenseAgree("yes")
  setLicenseAgreementUrl("https://gradle.com/terms-of-service")

  // Env variables from https://circleci.com/docs/2.0/env-vars/
  if (env("CI") != null) {
    logger.lifecycle("Running in CI environment, setting build scan attributes.")
    tag("CI")
    env("CIRCLE_BRANCH")?.let { tag(it) }
    env("CIRCLE_BUILD_NUM")?.let { value("Circle CI Build Number", it) }
    env("CIRCLE_BUILD_URL")?.let { link("Build URL", it) }
    env("CIRCLE_SHA1")?.let { value("Revision", it) }
    env("CIRCLE_COMPARE_URL")?.let { link("Diff", it) }
    env("CIRCLE_REPOSITORY_URL")?.let { value("Repository", it) }
    env("CIRCLE_PR_NUMBER")?.let { value("Pull Request Number", it) }
  }
}

var junitPlatformVersion: String by extra
junitPlatformVersion = "1.0.0-M3"
var junitJupiterVersion: String by extra
junitJupiterVersion = "5.0.0-M3"
var log4jVersion: String by extra
log4jVersion = "2.8.1"
var kotlinVersion: String by extra
kotlinVersion = "1.1.1"
var ratpackVersion: String by extra
ratpackVersion = "1.4.5"

fun ratpackModule(artifactName: String): Any = "io.ratpack:ratpack-$artifactName:$ratpackVersion"

subprojects {
  apply {
    plugin("org.jetbrains.kotlin.jvm")
    plugin("java-library")
    plugin("org.junit.platform.gradle.plugin")
  }

  convention.getPlugin(JavaPluginConvention::class.java).apply {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  dependencies {
    "api"(kotlinModule("stdlib-jre8", kotlinVersion))

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testImplementation(ratpackModule("test"))

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
    testRuntimeOnly("org.apache.logging.log4j:log4j-core:$log4jVersion")
    testRuntimeOnly("org.apache.logging.log4j:log4j-jul:$log4jVersion")
  }
}

project(":ratpack-core-kotlin") {
  dependencies {
    "api"(ratpackModule("core"))
  }
}
