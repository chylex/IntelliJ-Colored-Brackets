package com.chylex.intellij.coloredbrackets.action

import com.chylex.intellij.coloredbrackets.RainbowInfo
import com.chylex.intellij.coloredbrackets.settings.RainbowSettings
import com.chylex.intellij.coloredbrackets.util.alphaBlend
import com.intellij.codeInsight.highlighting.HighlightManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Font
import java.util.LinkedList

class ScopeHighlightingAction : AbstractScopeHighlightingAction() {
	
	override fun Editor.addHighlighter(
		highlightManager: HighlightManager,
		rainbowInfo: RainbowInfo,
	): Collection<RangeHighlighter> {
		val defaultBackground = EditorColorsManager.getInstance().globalScheme.defaultBackground
		val background = rainbowInfo.color.alphaBlend(defaultBackground, 0.2f)
		val attributes = TextAttributes(null, background, rainbowInfo.color, EffectType.BOXED, Font.PLAIN)
		val highlighters = LinkedList<RangeHighlighter>()
		highlightManager.addRangeHighlight(
			this,
			rainbowInfo.startOffset,
			rainbowInfo.endOffset,
			attributes, //create("ScopeHighlightingAction", attributes),
			false, //hideByTextChange
			RainbowSettings.instance.pressAnyKeyToRemoveTheHighlightingEffects, //hideByAnyKey
			highlighters
		)
		
		return highlighters
	}
	
}
