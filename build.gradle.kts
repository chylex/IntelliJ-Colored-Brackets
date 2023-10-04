@file:Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.8.0"
	id("org.jetbrains.intellij") version "1.15.0"
}

group = "com.chylex.intellij.rainbowbrackets"
version = "6.26-chylex-2"

val ideVersion = "2023.2.2"

idea {
	module {
		excludeDirs.add(file("gradle"))
		excludeDirs.add(file("libs"))
	}
}

repositories {
	mavenCentral()
	maven(url = "https://www.jetbrains.com/intellij-repository/releases")
	maven(url = "https://www.jetbrains.com/intellij-repository/snapshots")
}

val clion: Configuration by configurations.creating
val rider: Configuration by configurations.creating

dependencies {
	clion("com.jetbrains.intellij.clion:clion:$ideVersion")
	rider("com.jetbrains.intellij.rider:riderRD:$ideVersion")
	
	compileOnly(fileTree("libs"))
	
	testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
	testImplementation("io.kotest:kotest-assertions-core:5.5.5")
}

kotlin {
	jvmToolchain(17)
}

intellij {
	type.set("IU")
	version.set(ideVersion)
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
			"Dart:232.8660.129",                             // https://plugins.jetbrains.com/plugin/6351-dart/versions/stable
			"Pythonid:232.9921.47",                          // https://plugins.jetbrains.com/plugin/631-python/versions
			"com.jetbrains.php:232.9921.55",                 // https://plugins.jetbrains.com/plugin/6610-php/versions
			"com.jetbrains.sh:232.8660.88",                  // https://plugins.jetbrains.com/plugin/13122-shell-script/versions
			"org.intellij.scala:2023.2.23",                  // https://plugins.jetbrains.com/plugin/1347-scala/versions
			"org.jetbrains.plugins.go-template:232.9921.89", // https://plugins.jetbrains.com/plugin/10581-go-template/versions
			"org.jetbrains.plugins.ruby:232.9921.47",        // https://plugins.jetbrains.com/plugin/1293-ruby/versions
		)
	)
}

tasks.patchPluginXml {
	sinceBuild.set("232")
}

tasks.test {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions.freeCompilerArgs = listOf(
		"-Xjvm-default=all"
	)
}

fun createDownloadIdeTask(name: String, dependency: Configuration, configuration: CopySpec.() -> Unit): Sync {
	return tasks.create<Sync>(name) {
		group = "ides"
		outputs.upToDateWhen { false }
		
		from(dependency.map(::zipTree), configuration)
		into(file("libs/${dependency.name}"))
	}
}

val downloadIdeClion = createDownloadIdeTask("downloadIdeClion", clion) {
	include("plugins/cidr-base-plugin/**")
}

val downloadIdeRider = createDownloadIdeTask("downloadIdeRider", rider) {
	include("lib/app.jar")
}

tasks.create<Sync>("downloadExtraIdes") {
	group = "ides"
	
	dependsOn(downloadIdeClion)
	dependsOn(downloadIdeRider)
}
