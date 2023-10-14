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
			"Dart:232.8660.129",                             // https://plugins.jetbrains.com/plugin/6351-dart/versions/stable
			"Pythonid:232.9921.47",                          // https://plugins.jetbrains.com/plugin/631-python/versions
			"com.jetbrains.php:232.9921.55",                 // https://plugins.jetbrains.com/plugin/6610-php/versions
			"com.jetbrains.sh:232.8660.88",                  // https://plugins.jetbrains.com/plugin/13122-shell-script/versions
			"org.intellij.scala:2023.2.23",                  // https://plugins.jetbrains.com/plugin/1347-scala/versions
			"org.jetbrains.plugins.go-template:232.9921.89", // https://plugins.jetbrains.com/plugin/10581-go-template/versions
			"org.jetbrains.plugins.ruby:232.9921.47",        // https://plugins.jetbrains.com/plugin/1293-ruby/versions
		)
	)
}

tasks.buildSearchableOptions {
	enabled = true
}
