@file:Suppress("ConvertLambdaToReference")

import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
	kotlin("jvm")
	id("org.jetbrains.intellij.platform")
}

group = "com.chylex.intellij.coloredbrackets"
version = "1.3.0"

val ideaVersion = "2023.3"

allprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.jetbrains.intellij.platform")
	
	ext {
		set("ideaVersion", ideaVersion)
	}
	
	repositories {
		mavenCentral()
		
		intellijPlatform {
			defaultRepositories()
		}
	}
	
	intellijPlatform {
		pluginConfiguration {
			ideaVersion {
				sinceBuild.set("233")
				untilBuild.set(provider { null })
			}
		}
	}
	
	kotlin {
		jvmToolchain(17)
		
		compilerOptions {
			freeCompilerArgs = listOf(
				"-X" + "jvm-default=all",
			)
		}
	}
}

subprojects {
	intellijPlatform {
		buildSearchableOptions = false
	}
}

idea {
	module {
		excludeDirs.add(file("build"))
		excludeDirs.add(file("gradle"))
	}
}

dependencies {
	project(":api")
	
	intellijPlatform {
		@Suppress("DEPRECATION")
		intellijIdeaUltimate(ideaVersion)
		
		bundledPlugin("JavaScript")
		bundledPlugin("com.intellij.css")
		bundledPlugin("com.intellij.database")
		bundledPlugin("com.intellij.java")
		bundledPlugin("org.intellij.groovy")
		bundledPlugin("org.intellij.plugins.markdown")
		bundledPlugin("org.jetbrains.kotlin")
		bundledPlugin("org.jetbrains.plugins.yaml")
		
		plugin("Dart", "233.11799.172")                              // https://plugins.jetbrains.com/plugin/6351-dart/versions/stable
		plugin("PythonCore", "233.11799.300")                        // https://plugins.jetbrains.com/plugin/631-python/versions
		plugin("com.jetbrains.php", "233.11799.300")                 // https://plugins.jetbrains.com/plugin/6610-php/versions
		plugin("com.jetbrains.sh", "233.11799.165")                  // https://plugins.jetbrains.com/plugin/13122-shell-script/versions
		plugin("org.intellij.scala", "2023.3.19")                    // https://plugins.jetbrains.com/plugin/1347-scala/versions
		plugin("org.jetbrains.plugins.go-template", "233.11799.172") // https://plugins.jetbrains.com/plugin/10581-go-template/versions
		plugin("org.jetbrains.plugins.ruby", "233.11799.300")        // https://plugins.jetbrains.com/plugin/1293-ruby/versions
		
		testFramework(TestFrameworkType.Plugin.Java)
		
		pluginComposedModule(implementation(project(":api")))
		pluginComposedModule(implementation(project(":clion")))
		pluginComposedModule(implementation(project(":rider")))
	}
	
	testImplementation("junit:junit:4.13.2")
	testImplementation("io.kotest:kotest-assertions-core:5.8.0") {
		exclude(group = "org.jetbrains.kotlin")
	}
}

tasks.test {
	useJUnit()
}
