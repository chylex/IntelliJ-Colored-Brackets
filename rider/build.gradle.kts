val ideaVersion: String by project

dependencies {
	implementation(project(":base"))
	
	intellijPlatform {
		rider(ideaVersion) {
			useInstaller = false
		}
		
		bundledModule("intellij.rider.cpp.core.languages")
	}
}
