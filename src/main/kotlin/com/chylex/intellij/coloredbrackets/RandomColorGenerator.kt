package com.chylex.intellij.coloredbrackets

import com.chylex.intellij.coloredbrackets.color.Luminosity
import com.chylex.intellij.coloredbrackets.color.fromString
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.awt.Color

val mapper: ObjectMapper by lazy { jacksonObjectMapper() }

fun randomColor(options: String): Color {
	val ops: Map<String, String> = mapper.readValue(options)
	return com.chylex.intellij.coloredbrackets.color.randomColor(
		fromString(ops.getOrDefault("hue", "random")),
		Luminosity.valueOf(ops.getOrDefault("luminosity", "random"))
	)
}
