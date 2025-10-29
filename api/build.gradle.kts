val ideaVersion: String by project

dependencies {
	intellijPlatform {
		@Suppress("DEPRECATION")
		intellijIdeaUltimate(ideaVersion)
	}
}
