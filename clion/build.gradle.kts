val ideaVersion: String by project

dependencies {
	implementation(project(":api"))
	
	intellijPlatform {
		clion(ideaVersion)
		
		bundledPlugin("com.intellij.clion")
		// bundledPlugin("org.jetbrains.plugins.clion.radler") // Only in 2024.1 or newer. Worked around by only including the .xml file, and taking the implementation from Rider.
	}
}
