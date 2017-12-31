import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.gradle.api.plugins.ExtensionAware

import org.junit.platform.gradle.plugin.FiltersExtension
import org.junit.platform.gradle.plugin.EnginesExtension
import org.junit.platform.gradle.plugin.JUnitPlatformExtension


group = "jm.desprez"
version = "1.0-SNAPSHOT"

buildscript {
    val junit_version by extra { "5.0.2" }

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.0.2")
    }
}

apply {
    plugin("org.junit.platform.gradle.plugin")
}

plugins {
    application
    kotlin("jvm") version "1.2.0"
}

configure<JUnitPlatformExtension> {
    filters {
        engines {
            include("spek")
        }
    }
}

kotlin {
    // configure<org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension>
    experimental.coroutines = Coroutines.ENABLE
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

dependencies {
    val junit_version: String by extra

    compile(kotlin("stdlib"))
    //compile("io.vavr:vavr:0.9.1")
    compile("org.funktionale:funktionale-all:1.2")

    compile("com.github.salomonbrys.kodein:kodein:4.1.0")

    listOf("core", "server-jetty", "client-okhttp", "contract", "format-jackson").forEach { name ->
        compile("org.http4k:http4k-$name:3.6.1")
    }

    testCompile(group = "org.junit.jupiter", name = "junit-jupiter-api", version = junit_version)
    testRuntime(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = junit_version)

    testCompile("org.jetbrains.spek:spek-api:1.1.5")
    testRuntime("org.jetbrains.spek:spek-junit-platform-engine:1.1.5")

    testCompile("com.natpryce:hamkrest:1.4.2.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

// extension for configuration
fun JUnitPlatformExtension.filters(setup: FiltersExtension.() -> Unit) {
    when (this) {
        is ExtensionAware -> extensions.getByType(FiltersExtension::class.java).setup()
        else -> throw Exception("${this::class} must be an instance of ExtensionAware")
    }
}

fun FiltersExtension.engines(setup: EnginesExtension.() -> Unit) {
    when (this) {
        is ExtensionAware -> extensions.getByType(EnginesExtension::class.java).setup()
        else -> throw Exception("${this::class} must be an instance of ExtensionAware")
    }
}