package com.chylex.intellij.coloredbrackets.indents

import com.chylex.intellij.coloredbrackets.RainbowInfo
import com.chylex.intellij.coloredbrackets.util.alphaBlend
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.SoftWrap
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.impl.view.EditorPainter
import com.intellij.openapi.editor.impl.view.VisualLinesIterator
import com.intellij.openapi.editor.markup.CustomHighlighterRenderer
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.ui.paint.LinePainter2D
import com.intellij.util.text.CharArrayUtil
import java.awt.Graphics
import java.awt.Graphics2D
import kotlin.math.max

/**
 * From [com.intellij.codeInsight.daemon.impl.indentGuide.IndentGuideRenderer]
 * Commit history : https://sourcegraph.com/github.com/JetBrains/intellij-community/-/blob/platform/lang-impl/src/com/intellij/codeInsight/daemon/impl/IndentGuideRenderer.java#tab=history
 */
class RainbowIndentGuideRenderer(private val rainbowInfo: RainbowInfo) : CustomHighlighterRenderer {
	override fun paint(editor: Editor, highlighter: RangeHighlighter, g: Graphics) {
		if (editor !is EditorEx) {
			return
		}
		
		val startOffset = highlighter.startOffset
		val doc = highlighter.document
		if (startOffset >= doc.textLength) {
			return
		}
		
		val endOffset = highlighter.endOffset
		
		var off: Int
		var startLine = doc.getLineNumber(startOffset)
		
		val chars = doc.charsSequence
		do {
			val start = doc.getLineStartOffset(startLine)
			val end = doc.getLineEndOffset(startLine)
			off = CharArrayUtil.shiftForward(chars, start, end, " \t")
			startLine--
		} while (startLine > 1 && off < doc.textLength && chars[off] == '\n')
		
		val startPosition = editor.offsetToVisualPosition(off)
		val indentColumn = startPosition.column
		
		if (indentColumn <= 0) {
			return
		}
		
		val foldingModel = editor.foldingModel
		if (foldingModel.isOffsetCollapsed(off)) {
			return
		}
		
		val headerRegion = foldingModel.getCollapsedRegionAtOffset(doc.getLineEndOffset(doc.getLineNumber(off)))
		val tailRegion = foldingModel.getCollapsedRegionAtOffset(doc.getLineStartOffset(doc.getLineNumber(endOffset)))
		
		if (tailRegion != null && tailRegion === headerRegion) {
			return
		}
		
		val guide = editor.indentsModel.caretIndentGuide
		val selected = if (guide != null) {
			val caretModel = editor.caretModel
			val caretOffset = caretModel.offset
			caretOffset in off until endOffset && caretModel.logicalPosition.column == indentColumn
		}
		else false
		
		val lineHeight = editor.lineHeight
		val start = editor.visualPositionToXY(startPosition)
		start.y += lineHeight
		val endPosition = editor.offsetToVisualPosition(endOffset)
		val end = editor.visualPositionToXY(endPosition)
		var maxY = end.y
		if (endPosition.line == editor.offsetToVisualPosition(doc.textLength).line) {
			maxY += lineHeight
		}
		
		val clip = g.clipBounds
		if (clip != null) {
			if (clip.y >= maxY || clip.y + clip.height <= start.y) {
				return
			}
			maxY = StrictMath.min(maxY, clip.y + clip.height)
		}
		if (start.y >= maxY) {
			return
		}
		
		val targetX = max(0, start.x + EditorPainter.getIndentGuideShift(editor)).toDouble()
		g.color = if (selected) {
			rainbowInfo.color
		}
		else {
			val defaultBackground = editor.colorsScheme.defaultBackground
			rainbowInfo.color.alphaBlend(defaultBackground, 0.2f)
		}
		
		// There is a possible case that indent line intersects soft wrap-introduced text. Example:
		//     this is a long line <soft-wrap>
		// that| is soft-wrapped
		//     |
		//     | <- vertical indent
		//
		// Also it's possible that no additional intersections are added because of soft wrap:
		//     this is a long line <soft-wrap>
		//     |   that is soft-wrapped
		//     |
		//     | <- vertical indent
		// We want to use the following approach then:
		//     1. Show only active indent if it crosses soft wrap-introduced text;
		//     2. Show indent as is if it doesn't intersect with soft wrap-introduced text;
		val softWraps = editor.softWrapModel.registeredSoftWraps
		if (selected || softWraps.isEmpty()) {
			LinePainter2D.paint(g as Graphics2D, targetX, start.y.toDouble(), targetX, maxY - 1.toDouble())
		}
		else {
			var startY = start.y
			var startVisualLine = startPosition.line + 1
			if (clip != null && startY < clip.y) {
				startY = clip.y
				startVisualLine = editor.yToVisualLine(clip.y)
			}
			val it = VisualLinesIterator(editor as EditorImpl, startVisualLine)
			while (!it.atEnd()) {
				val currY: Int = it.y
				if (currY >= startY) {
					if (currY >= maxY) break
					if (it.startsWithSoftWrap()) {
						val softWrap: SoftWrap = softWraps[it.startOrPrevWrapIndex]
						if (softWrap.indentInColumns < indentColumn) {
							if (startY < currY) {
								LinePainter2D.paint((g as Graphics2D), targetX, startY.toDouble(), targetX, currY - 1.toDouble())
							}
							startY = currY + lineHeight
						}
					}
				}
				it.advance()
			}
			if (startY < maxY) {
				LinePainter2D.paint((g as Graphics2D), targetX, startY.toDouble(), targetX, maxY - 1.toDouble())
			}
		}
	}
}
