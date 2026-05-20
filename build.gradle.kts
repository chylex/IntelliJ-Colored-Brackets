@file:Suppress("ConvertLambdaToReference")

plugins {
	kotlin("jvm")
	id("org.jetbrains.intellij.platform")
}

group = "com.chylex.intellij.coloredbrackets"
version = "1.3.0"

val ideaVersion = "2025.3"

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
		sandboxContainer.set(layout.buildDirectory.map { it.dir("idea-sandbox") })
		
		pluginConfiguration {
			ideaVersion {
				sinceBuild.set("253")
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
	version = rootProject.version
	
	intellijPlatform {
		buildSearchableOptions = false
	}
}

idea {
	module {
		excludeDirs.add(file(".kotlin"))
		excludeDirs.add(file("build"))
		excludeDirs.add(file("gradle"))
	}
}

dependencies {
	intellijPlatform {
		@Suppress("DEPRECATION")
		intellijIdeaUltimate(ideaVersion)
		
		pluginComposedModule(implementation(project(":base")))
		pluginComposedModule(implementation(project(":clion")))
		pluginComposedModule(implementation(project(":rider")))
	}
}

intellijPlatform {
	buildSearchableOptions.set(true)
}

tasks.test {
	useJUnit()
}
