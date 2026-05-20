package com.chylex.intellij.coloredbrackets.action

import com.chylex.intellij.coloredbrackets.RainbowHighlighter
import com.chylex.intellij.coloredbrackets.RainbowInfo
import com.chylex.intellij.coloredbrackets.settings.RainbowSettings
import com.intellij.codeInsight.highlighting.HighlightManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.RangeHighlighter
import java.util.LinkedList

class ScopeHighlightingAction : AbstractScopeHighlightingAction() {
	
	override fun Editor.addHighlighter(
		editor: Editor,
		highlightManager: HighlightManager,
		rainbowInfo: RainbowInfo,
	): Collection<RangeHighlighter> {
		val attributesKey = RainbowHighlighter.updateScopeHighlightingAttributes(editor.colorsScheme, rainbowInfo)
		val highlighters = LinkedList<RangeHighlighter>()
		
		highlightManager.addRangeHighlight(
			this,
			rainbowInfo.startOffset,
			rainbowInfo.endOffset,
			attributesKey,
			false,
			RainbowSettings.instance.pressAnyKeyToRemoveTheHighlightingEffects,
			highlighters
		)
		
		return highlighters
	}
}
