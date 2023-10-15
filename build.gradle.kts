@file:Suppress("SpellCheckingInspection")

import org.jetbrains.intellij.IntelliJPluginExtension
import org.jetbrains.intellij.tasks.BuildPluginTask
import org.jetbrains.intellij.tasks.BuildSearchableOptionsTask
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	idea
	kotlin("jvm")
	id("org.jetbrains.intellij") apply false
}

group = "com.chylex.intellij.rainbowbrackets"
version = "6.26-chylex-2"

val ideVersion = "2023.2.2"
val ideBuild = "232"

idea {
	module {
		excludeDirs.add(file("gradle"))
	}
}

dependencies {
	implementation(project(":clion"))
	implementation(project(":idea"))
	implementation(project(":rider"))
}

subprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.jetbrains.intellij")
	
	group = rootProject.group
	version = rootProject.version
	
	repositories {
		mavenCentral()
		maven(url = "https://www.jetbrains.com/intellij-repository/releases")
		maven(url = "https://www.jetbrains.com/intellij-repository/snapshots")
	}
	
	dependencies {
		testImplementation("junit:junit:4.13.2")
		testImplementation("io.kotest:kotest-assertions-core:5.5.5")
	}
	
	kotlin {
		jvmToolchain(17)
	}
	
	configure<IntelliJPluginExtension> {
		version.set(ideVersion)
		updateSinceUntilBuild.set(false)
	}
	
	tasks.withType<KotlinCompile> {
		kotlinOptions.freeCompilerArgs = listOf(
			"-Xjvm-default=all"
		)
	}
	
	tasks.test {
		useJUnit()
	}
	
	tasks.getByName<BuildSearchableOptionsTask>("buildSearchableOptions") {
		enabled = false
	}
	
	tasks.getByName<PatchPluginXmlTask>("patchPluginXml") {
		sinceBuild.set(ideBuild)
	}
	
	tasks.getByName<BuildPluginTask>("buildPlugin") {
		eachFile {
			name = name.replaceFirst("instrumented-", "instrumented-RainbowBrackets-")
			relativePath.segments[0] = "RainbowBrackets"
		}
		
		includeEmptyDirs = false
	}
}

tasks.register<Zip>("buildPlugin") {
	group = "intellij"
	
	for (project in listOf("clion", "idea", "rider")) {
		val buildPlugin = project(":$project").tasks.getByName("buildPlugin")
		val outputs = buildPlugin.outputs.files.map(::zipTree)
		
		dependsOn(buildPlugin)
		
		from(outputs) {
			include("RainbowBrackets/lib/instrumented-RainbowBrackets-$project-$version.jar")
		}
		
		if (project == "idea") {
			from(outputs) {
				include("RainbowBrackets/lib/searchableOptions-$version.jar")
			}
		}
	}
	
	destinationDirectory = layout.buildDirectory.dir("distributions")
}
