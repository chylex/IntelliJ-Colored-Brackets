rootProject.name = "ColoredBrackets"

pluginManagement {
	plugins {
		kotlin("jvm") version "1.9.21"
		id("org.jetbrains.intellij") version "1.17.4"
	}
}

include("clion")
include("rider")
