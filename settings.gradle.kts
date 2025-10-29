rootProject.name = "ColoredBrackets"

pluginManagement {
	plugins {
		kotlin("jvm") version "1.9.21"
		id("org.jetbrains.intellij.platform") version "2.9.0"
	}
}

include("api")
include("clion")
include("rider")
