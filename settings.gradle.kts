rootProject.name = "RainbowBrackets"

pluginManagement {
	repositories {
		maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
		gradlePluginPortal()
	}
	
	plugins {
		kotlin("jvm") version "1.8.0"
		id("org.jetbrains.intellij") version "1.15.0"
	}
}

include("clion")
include("idea")
include("rider")
