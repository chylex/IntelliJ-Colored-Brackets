package com.chylex.intellij.coloredbrackets.indents

import com.chylex.intellij.coloredbrackets.RainbowInfo
import com.chylex.intellij.coloredbrackets.settings.RainbowSettings
import com.chylex.intellij.coloredbrackets.util.endOffset
import com.chylex.intellij.coloredbrackets.util.findNextSibling
import com.chylex.intellij.coloredbrackets.util.findPrevSibling
import com.chylex.intellij.coloredbrackets.util.lineNumber
import com.chylex.intellij.coloredbrackets.util.startOffset
import com.intellij.codeHighlighting.TextEditorHighlightingPass
import com.intellij.ide.actions.ToggleZenModeAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.IndentGuideDescriptor
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.editor.impl.IndentsModelImpl
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.Condition
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.Segment
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlToken
import com.intellij.psi.xml.XmlTokenType
import com.intellij.util.DocumentUtil
import java.util.Collections

class RainbowIndentsPass internal constructor(
	myFile: PsiFile,
	private val myEditor: Editor,
) : TextEditorHighlightingPass(myFile.project, myEditor.document, false), DumbAware {
	
	private val defaultIndentsDescriptors = DefaultIndentsDescriptors(myFile, myEditor)
	
	@Volatile
	private var myRanges = emptyList<TextRange>()
	
	@Volatile
	private var myDescriptors = emptyList<IndentGuideDescriptor>()
	
	override fun doCollectInformation(progress: ProgressIndicator) {
		val stamp = myEditor.getUserData(LAST_TIME_INDENTS_BUILT)
		if (stamp != null && stamp == nowStamp()) {
			return
		}
		
		myDescriptors = getDescriptorsFromIndentsModel() ?: defaultIndentsDescriptors.buildDescriptors() 
		
		val ranges = ArrayList<TextRange>()
		for (descriptor in myDescriptors) {
			ProgressManager.checkCanceled()
			val endOffset = if (descriptor.endLine < document.lineCount) {
				document.getLineStartOffset(descriptor.endLine)
			}
			else {
				document.textLength
			}
			ranges.add(TextRange(document.getLineStartOffset(descriptor.startLine), endOffset))
		}
		
		Collections.sort(ranges, Segment.BY_START_OFFSET_THEN_END_OFFSET)
		myRanges = ranges
	}
	
	private fun nowStamp(): Long = if (isRainbowIndentGuidesShown())
		document.modificationStamp xor (EditorUtil.getTabSize(myEditor).toLong() shl 24)
	else
		-1
	
	private fun getDescriptorsFromIndentsModel(): List<IndentGuideDescriptor>? {
		if (!isRainbowIndentGuidesShown() || document.textLength == 0) {
			return emptyList()
		}
		
		val indentsModel = myEditor.indentsModel as? IndentsModelImpl ?: return null
		return indentsModel.indents.takeUnless { it.isEmpty() }
	}
	
	override fun doApplyInformationToEditor() {
		val stamp = myEditor.getUserData(LAST_TIME_INDENTS_BUILT)
		val nowStamp = nowStamp()
		
		if (stamp == nowStamp) {
			return
		}
		
		myEditor.putUserData(LAST_TIME_INDENTS_BUILT, nowStamp)
		
		val oldHighlighters = myEditor.getUserData(INDENT_HIGHLIGHTERS_IN_EDITOR_KEY)
		if (nowStamp == -1L) {
			if (oldHighlighters != null) {
				for (oldHighlighter in oldHighlighters) {
					oldHighlighter.dispose()
				}
				oldHighlighters.clear()
			}
			return
		}
		
		val newHighlighters = ArrayList<RangeHighlighter>()
		val markupModel = myEditor.markupModel
		var curRange = 0
		
		if (oldHighlighters != null) {
			// after document change some range highlighters could have become invalid, or the order could have been broken
			oldHighlighters.sortWith(Comparator.comparing { h: RangeHighlighter -> !h.isValid }
				.thenComparing(Segment.BY_START_OFFSET_THEN_END_OFFSET))
			
			var curHighlight = 0
			while (curRange < myRanges.size && curHighlight < oldHighlighters.size) {
				val range = myRanges[curRange]
				val highlighter = oldHighlighters[curHighlight]
				if (!highlighter.isValid) break
				
				val cmp = compare(range, highlighter)
				when {
					cmp < 0 -> {
						createHighlighter(markupModel, range)?.let(newHighlighters::add)
						curRange++
					}
					
					cmp > 0 -> {
						highlighter.dispose()
						curHighlight++
					}
					
					else    -> {
						newHighlighters.add(highlighter)
						curHighlight++
						curRange++
					}
				}
			}
			
			while (curHighlight < oldHighlighters.size) {
				val highlighter = oldHighlighters[curHighlight]
				if (!highlighter.isValid) break
				highlighter.dispose()
				curHighlight++
			}
		}
		
		val startRangeIndex = curRange
		DocumentUtil.executeInBulk(document, myRanges.size > 10000) {
			for (i in startRangeIndex until myRanges.size) {
				createHighlighter(markupModel, myRanges[i])?.let(newHighlighters::add)
			}
		}
		
		myEditor.putUserData(INDENT_HIGHLIGHTERS_IN_EDITOR_KEY, newHighlighters)
	}
	
	private fun createHighlighter(mm: MarkupModel, range: TextRange): RangeHighlighter? {
		val rainbowInfo = getRainbowInfo(myEditor, range.startOffset, range.endOffset) ?: return null
		
		return mm.addRangeHighlighter(
			range.startOffset,
			range.endOffset,
			0,
			null,
			HighlighterTargetArea.EXACT_RANGE
		).apply {
			customRenderer = RainbowIndentGuideRenderer(rainbowInfo)
		}
	}
	
	private fun isRainbowIndentGuidesShown(): Boolean {
		if (!myEditor.settings.isIndentGuidesShown) {
			return false
		}
		
		if (RainbowSettings.instance.disableRainbowIndentsInZenMode && ToggleZenModeAction.isZenModeEnabled(myProject)) {
			return false
		}
		
		return RainbowSettings.instance.isRainbowEnabled && RainbowSettings.instance.isShowRainbowIndentGuides
	}
	
	companion object {
		private val INDENT_HIGHLIGHTERS_IN_EDITOR_KEY = Key.create<MutableList<RangeHighlighter>>("_INDENT_HIGHLIGHTERS_IN_EDITOR_KEY_")
		private val LAST_TIME_INDENTS_BUILT = Key.create<Long>("_LAST_TIME_INDENTS_BUILT_")
		
		private val XML_TAG_PARENT_CONDITION = Condition<PsiElement> { it is XmlTag }
		
		private val XML_END_TAG_START_CONDITION: (PsiElement) -> Boolean = { element ->
			element is XmlToken && element.tokenType == XmlTokenType.XML_END_TAG_START
		}
		
		private val XML_TAG_END_CONDITION: (PsiElement) -> Boolean = { element ->
			element is XmlToken && element.tokenType == XmlTokenType.XML_TAG_END
		}
		
		private fun compare(r: TextRange, h: RangeHighlighter): Int {
			val answer = r.startOffset - h.startOffset
			return if (answer != 0) answer else r.endOffset - h.endOffset
		}
		
		private fun getRainbowInfo(editor: Editor, startOffset: Int, endOffset: Int): RainbowInfo? {
			val project = editor.project ?: return null
			val document = editor.document
			
			val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return null
			var element = try {
				psiFile.findElementAt(endOffset)?.parent ?: return null
			} catch (_: Throwable) {
				return null
			}
			
			var rainbowInfo = RainbowInfo.RAINBOW_INFO_KEY[element]
			if (rainbowInfo == null && psiFile is XmlFile && element !is XmlTag) {
				element = PsiTreeUtil.findFirstParent(element, true, XML_TAG_PARENT_CONDITION) ?: return null
				rainbowInfo = RainbowInfo.RAINBOW_INFO_KEY[element] ?: return null
			}
			
			if (!element.isValid || !checkBoundary(document, element, startOffset, endOffset)) {
				return null
			}
			
			return rainbowInfo
		}
		
		private fun checkBoundary(document: Document, element: PsiElement, startOffset: Int, endOffset: Int): Boolean {
			val elementStartLine = document.lineNumber(element.startOffset) ?: return false
			val highlighterStartLine = document.lineNumber(startOffset) ?: return false
			
			var xmlStartTagEndLine: Int? = null
			var xmlEndTagStartLine: Int? = null
			
			val isValidStartBoundary = if (element is XmlTag) {
				/*
				 *     <tag                  // [*] element & highlighter start line
				 *     | <- vertical indent
				 *     >                     // [*] highlighter start/end line, start tag end line
				 *     | <- vertical indent
				 *     </tag                 // [*] highlighter start/end line, end tag start line
				 *     | <- vertical indent
				 *     >                     // [ ] element/highlighter end line
				 */
				xmlStartTagEndLine = element.getStartTagEndLineNumber(document)
				xmlEndTagStartLine = element.getEndTagStartLineNumber(document)
				
				highlighterStartLine == elementStartLine ||
					highlighterStartLine == xmlStartTagEndLine ||
					highlighterStartLine == xmlEndTagStartLine
			}
			else {
				/*
				 * Element start line > Highlighter start line:
				 *     function foo(arg1,   // highlighter start line
				 *     |            arg2) { // element start line
				 *     | <- vertical indent
				 *     }                    // element & highlighter end line
				 */
				elementStartLine >= highlighterStartLine
			}
			if (!isValidStartBoundary) {
				return false
			}
			
			val elementEndLine = document.lineNumber(element.endOffset) ?: return false
			val highlighterEndLine = document.lineNumber(endOffset) ?: return false
			val isValidEndBoundary = if (element is XmlTag) {
				/*
				 *     <tag                  // [ ] element & highlighter start line
				 *     | <- vertical indent
				 *     >                     // [*] highlighter start/end line, start tag end line
				 *     | <- vertical indent
				 *     </tag                 // [*] highlighter start/end line, end tag start line
				 *     | <- vertical indent
				 *     >                     // [*] element/highlighter end line
				 */
				highlighterEndLine == elementEndLine ||
					highlighterEndLine == xmlStartTagEndLine ||
					highlighterEndLine == xmlEndTagStartLine
			}
			else {
				/*
				 * Element end line != Highlighter end line:
				 *     function foo() {     // element & highlighter start line
				 *     | <- vertical indent
				 *   var bar = "bar";       // highlighter end line
				 *     }                    // element end line
				 */
				elementEndLine == highlighterEndLine
			}
			if (!isValidEndBoundary) {
				return false
			}
			
			return true
		}
		
		private fun XmlTag.getStartTagEndLineNumber(document: Document): Int? =
			firstChild?.findNextSibling(XML_TAG_END_CONDITION)?.let { document.lineNumber(it.startOffset) }
		
		private fun XmlTag.getEndTagStartLineNumber(document: Document): Int? =
			lastChild?.findPrevSibling(XML_END_TAG_START_CONDITION)?.let { document.lineNumber(it.startOffset) }
	}
}
