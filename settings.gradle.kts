rootProject.name = "ColoredBrackets"

pluginManagement {
	plugins {
		kotlin("jvm") version "1.9.21"
		id("org.jetbrains.intellij.platform") version "2.18.1"
	}
}

include(":base")
include(":clion")
include(":rider")
