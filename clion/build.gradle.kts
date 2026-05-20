val ideaVersion: String by project

dependencies {
	implementation(project(":base"))
	
	runtimeOnly(project(":rider")) // Support for CLion Nova.
	
	intellijPlatform {
		clion(ideaVersion)
		
		bundledPlugin("com.intellij.clion")
		bundledPlugin("org.jetbrains.plugins.clion.radler")
	}
}
