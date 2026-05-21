package com.chylex.intellij.coloredbrackets

import com.chylex.intellij.coloredbrackets.visitor.RainbowHighlightVisitor
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import java.awt.Color

fun List<HighlightInfo>.getBrackets(): Array<Color> {
	return this
		.filter { it.toolId?.let { id -> id is Class<*> && RainbowHighlightVisitor::class.java.isAssignableFrom(id) } == true }
		.map { it.forcedTextAttributesKey.defaultAttributes.foregroundColor }
		.toTypedArray()
}

fun roundLevel(level: Int) = RainbowHighlighter.getRainbowColor(RainbowHighlighter.NAME_ROUND_BRACKETS, level)

fun squigglyLevel(level: Int) = RainbowHighlighter.getRainbowColor(RainbowHighlighter.NAME_SQUIGGLY_BRACKETS, level)

fun angleLevel(level: Int) = RainbowHighlighter.getRainbowColor(RainbowHighlighter.NAME_ANGLE_BRACKETS, level)

fun squareLevel(level: Int) = RainbowHighlighter.getRainbowColor(RainbowHighlighter.NAME_SQUARE_BRACKETS, level)
