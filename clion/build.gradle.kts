plugins {
	id("org.jetbrains.intellij")
}

intellij {
	type.set("CL")
	
	plugins.set(listOf(
		// Built-in
		"cidr-base-plugin"
	))
}

dependencies {
	implementation(project(":idea"))
}
