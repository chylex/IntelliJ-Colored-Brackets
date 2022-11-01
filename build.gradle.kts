@file:Suppress("ConvertLambdaToReference")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.9.21"
	id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.chylex.intellij.coloredbrackets"
version = "0.0.1"

repositories {
	mavenCentral()
}

intellij {
	type.set("IU")
	version.set("2023.3")
	updateSinceUntilBuild.set(false)
	
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

kotlin {
	jvmToolchain(17)
}

dependencies {
	compileOnly(fileTree("libs"))
	
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

tasks.withType<KotlinCompile> {
	kotlinOptions.freeCompilerArgs = listOf(
		"-Xjvm-default=all"
	)
}
