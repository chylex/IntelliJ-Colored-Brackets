import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.extensions.excludeCoroutines
import org.jetbrains.intellij.platform.gradle.extensions.excludeKotlinStdlib

val ideaVersion: String by project

dependencies {
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
		
		compatiblePlugin("PythonCore")
		compatiblePlugin("com.jetbrains.php")
		compatiblePlugin("com.jetbrains.plugins.jade")
		compatiblePlugin("com.jetbrains.sh")
		compatiblePlugin("org.intellij.scala")
		compatiblePlugin("org.jetbrains.plugins.go-template")
		compatiblePlugin("org.jetbrains.plugins.ruby")
		
		plugin("Dart:504.0.0") // https://plugins.jetbrains.com/plugin/6351-dart/versions/stable
		
		testFramework(TestFrameworkType.Platform)
		testFramework(TestFrameworkType.Plugin.Java)
		testFramework(TestFrameworkType.Plugin.JavaScript)
	}
	
	testImplementation("junit:junit:4.13.2")
	testImplementation("io.kotest:kotest-assertions-core:5.8.0") {
		excludeKotlinStdlib()
		excludeCoroutines()
	}
}

intellijPlatform {
	buildSearchableOptions = true
}
