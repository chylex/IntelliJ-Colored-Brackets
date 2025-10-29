val ideaVersion: String by project

dependencies {
	implementation(project(":api"))
	
	intellijPlatform {
		rider(ideaVersion) {
			useInstaller = false
		}
	}
}
