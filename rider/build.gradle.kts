plugins {
	id("org.jetbrains.intellij")
}

intellij {
	type.set("RD")
}

dependencies {
	implementation(project(":idea"))
}
