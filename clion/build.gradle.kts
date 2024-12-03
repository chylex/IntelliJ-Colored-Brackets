intellij {
	type.set("CL")
	
	plugins.set(listOf(
		// Built-in
		"cidr-base-plugin",
		//"org.jetbrains.plugins.clion.radler" // Only in 2024.1 or newer. Worked around by only including the .xml file, and taking the implementation from Rider.
	))
}

dependencies {
	implementation(rootProject)
}
