package com.chylex.intellij.coloredbrackets.listener

import com.chylex.intellij.coloredbrackets.RainbowHighlighter
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsScheme

class RainbowColorsSchemeListener : EditorColorsListener {
	
	override fun globalSchemeChange(scheme: EditorColorsScheme?) {
		scheme?.let { RainbowHighlighter.fixHighlighting(it) }
	}
}
