plugins {
	id("org.jetbrains.intellij")
}

intellij {
	type.set("IU")
	
	plugins.set(
		listOf(
			// Built-in
			"Groovy",
			"JavaScript",
			"com.intellij.css",
			"com.intellij.database",
			"com.intellij.java",
			"org.intellij.plugins.markdown",
			"org.jetbrains.kotlin",
			"org.jetbrains.plugins.yaml",
			// Downloaded
			"Dart:233.11799.172",                              // https://plugins.jetbrains.com/plugin/6351-dart/versions/stable
			"Pythonid:233.11799.300",                          // https://plugins.jetbrains.com/plugin/631-python/versions
			"com.jetbrains.php:233.11799.300",                 // https://plugins.jetbrains.com/plugin/6610-php/versions
			"com.jetbrains.sh:233.11799.165",                  // https://plugins.jetbrains.com/plugin/13122-shell-script/versions
			"org.intellij.scala:2023.3.17",                    // https://plugins.jetbrains.com/plugin/1347-scala/versions
			"org.jetbrains.plugins.go-template:233.11799.172", // https://plugins.jetbrains.com/plugin/10581-go-template/versions
			"org.jetbrains.plugins.ruby:233.11799.300",        // https://plugins.jetbrains.com/plugin/1293-ruby/versions
		)
	)
}

tasks.buildSearchableOptions {
	enabled = true
}
