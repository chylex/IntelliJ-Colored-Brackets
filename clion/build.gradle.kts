val ideaVersion: String by project

dependencies {
	implementation(project(":base"))
	
	intellijPlatform {
		clion(ideaVersion)
		
		compatiblePlugin("com.intellij.cidr.lang")
	}
}
