package com.chylex.intellij.coloredbrackets.action

import com.chylex.intellij.coloredbrackets.RainbowHighlighter
import com.chylex.intellij.coloredbrackets.RainbowInfo
import com.chylex.intellij.coloredbrackets.settings.RainbowSettings
import com.intellij.codeInsight.highlighting.HighlightManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.RangeHighlighter
import java.util.LinkedList

class ScopeOutsideHighlightingRestrainAction : AbstractScopeHighlightingAction() {
	
	override fun Editor.addHighlighter(
		editor: Editor,
		highlightManager: HighlightManager,
		rainbowInfo: RainbowInfo,
	): Collection<RangeHighlighter> {
		val attributesKey = RainbowHighlighter.updateScopeOutsideHighlightingAttributes(editor.colorsScheme)
		val highlighters = LinkedList<RangeHighlighter>()
		
		val startOffset = rainbowInfo.startOffset
		val hideByAnyKey = RainbowSettings.instance.pressAnyKeyToRemoveTheHighlightingEffects
		
		if (startOffset > 0) {
			highlightManager.addRangeHighlight(
				this,
				0,
				startOffset,
				attributesKey,
				false,
				hideByAnyKey,
				highlighters
			)
		}
		
		val endOffset = rainbowInfo.endOffset
		val lastOffset = document.textLength
		if (endOffset < lastOffset) {
			highlightManager.addRangeHighlight(
				this,
				endOffset,
				lastOffset,
				attributesKey,
				false,
				hideByAnyKey,
				highlighters
			)
		}
		
		return highlighters
	}
}
