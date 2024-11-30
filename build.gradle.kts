@file:Suppress("ConvertLambdaToReference")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.io.path.Path

plugins {
	kotlin("jvm")
	id("org.jetbrains.intellij")
}

group = "com.chylex.intellij.coloredbrackets"
version = "0.0.1"

allprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.jetbrains.intellij")
	
	repositories {
		mavenCentral()
	}

	intellij {
		version.set("2023.3")
		updateSinceUntilBuild.set(false)
	}
	
	kotlin {
		jvmToolchain(17)
	}
	
	tasks.withType<KotlinCompile> {
		kotlinOptions.freeCompilerArgs = listOf(
			"-Xjvm-default=all"
		)
	}
}

subprojects {
	tasks.buildSearchableOptions {
		enabled = false
	}
}

idea {
	module {
		excludeDirs.add(file("gradle"))
	}
}

intellij {
	type.set("IU")
	
	plugins.set(
		listOf(
			// Built-in
			"Groovy",
			"JavaScript",
			"com.intellij.css",
			"com.intellij.database",
			"com.intellij.java",
			"org.intellij.plugins.markdown",
			"org.jetbrains.kotlin",
			"org.jetbrains.plugins.yaml",
			// Downloaded
			"Dart:233.11799.172",                              // https://plugins.jetbrains.com/plugin/6351-dart/versions/stable
			"Pythonid:233.11799.300",                          // https://plugins.jetbrains.com/plugin/631-python/versions
			"com.jetbrains.php:233.11799.300",                 // https://plugins.jetbrains.com/plugin/6610-php/versions
			"com.jetbrains.sh:233.11799.165",                  // https://plugins.jetbrains.com/plugin/13122-shell-script/versions
			"org.intellij.scala:2023.3.19",                    // https://plugins.jetbrains.com/plugin/1347-scala/versions
			"org.jetbrains.plugins.go-template:233.11799.172", // https://plugins.jetbrains.com/plugin/10581-go-template/versions
			"org.jetbrains.plugins.ruby:233.11799.300",        // https://plugins.jetbrains.com/plugin/1293-ruby/versions
		)
	)
}

dependencies {
	testImplementation("junit:junit:4.13.2")
	testImplementation("io.kotest:kotest-assertions-core:5.8.0") {
		exclude(group = "org.jetbrains.kotlin")
	}
}

tasks.patchPluginXml {
	sinceBuild.set("233")
}

tasks.test {
	useJUnit()
}

tasks.buildPlugin {
	val projectName = rootProject.name
	val instrumentedJarName = "instrumented-$projectName-$version"
	
	for (ide in listOf("clion", "rider")) {
		val task = project(":$ide").tasks.buildPlugin
		
		dependsOn(task)
		
		from(task.map { it.outputs.files.map(::zipTree) }) {
			include("$ide/lib/instrumented-$ide.jar")
			into("lib")
			
			eachFile {
				val newName = name.replace("instrumented-", "${instrumentedJarName}-")
				val newPath = relativePath.segments.dropLast(3).plus(newName)
				relativePath = RelativePath(true, *newPath.toTypedArray())
			}
			
			includeEmptyDirs = false
		}
	}
	
	doLast {
		val expectedPaths = listOf(
			Path(projectName, "lib", "instrumented-$projectName-$version-clion.jar"),
			Path(projectName, "lib", "instrumented-$projectName-$version-rider.jar"),
			Path(projectName, "lib", "instrumented-$projectName-$version.jar"),
			Path(projectName, "lib", "searchableOptions-$version.jar"),
		)
		
		val jarFiles = zipTree(outputs.files.singleFile)
		
		for (expectedPath in expectedPaths) {
			val found = jarFiles.find { it.toPath().endsWith(expectedPath) }
			checkNotNull(found) { "Expected path not found: $expectedPath" }
		}
	}
}
