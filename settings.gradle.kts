rootProject.name = "ColoredBrackets"

pluginManagement {
	plugins {
		kotlin("jvm") version "2.2.20"
		id("org.jetbrains.intellij.platform") version "2.18.1"
	}
}

include(":base")
include(":clion")
include(":rider")
