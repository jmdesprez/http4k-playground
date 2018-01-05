import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.gradle.api.plugins.ExtensionAware
import org.junit.platform.gradle.plugin.FiltersExtension
import org.junit.platform.gradle.plugin.EnginesExtension
import org.junit.platform.gradle.plugin.JUnitPlatformExtension
import org.gradle.jvm.tasks.Jar

buildscript {
    repositories {
        mavenLocal()
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

application {
    group = "jm.desprez"
    version = "1.0-SNAPSHOT"
    applicationName = "HTTP-4K Demo"
    mainClassName = "$group.UserServerKt"
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

    compile(kotlin("stdlib"))
    //compile("io.vavr:vavr:0.9.1")
    compile("org.funktionale:funktionale-all:1.2")

    compile("com.github.salomonbrys.kodein:kodein:4.1.0")

    listOf("core", "server-jetty", "client-okhttp", "contract", "format-jackson").forEach { name ->
        compile("org.http4k:http4k-$name:3.6.1")
    }

    listOf("api" to "testCompile", "engine" to "testRuntime").forEach { (name, configurationName) ->
        // add(configurationName, create(group = "org.junit.jupiter", name = "junit-jupiter-$name", version = "5.0.2"))
        add(configurationName, "org.junit.jupiter:junit-jupiter-$name:5.0.2")
    }

    listOf("api" to "testCompile", "junit-platform-engine" to "testRuntime").forEach { (name, configurationName) ->
        add(configurationName, "org.jetbrains.spek:spek-$name:1.1.5")
    }

    testCompile("com.natpryce:hamkrest:1.4.2.2")
}

val fatJar = task("fatJar", type = Jar::class) {
    baseName = "${project.name}-fat"
    manifest {
        attributes["Implementation-Title"] = application.applicationName
        attributes["Implementation-Version"] = project.version
        attributes["Main-Class"] = application.mainClassName
    }
    from(configurations.runtime.map({ if (it.isDirectory) it as Any else zipTree(it) }))
    with(tasks["jar"] as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
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