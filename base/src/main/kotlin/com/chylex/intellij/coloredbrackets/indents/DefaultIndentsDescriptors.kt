package com.chylex.intellij.coloredbrackets.indents

import com.intellij.codeInsight.highlighting.BraceMatchingUtil
import com.intellij.codeInsight.highlighting.CodeBlockSupportHandler
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.IndentGuideDescriptor
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.progress.ProgressManager
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.TokenSet
import com.intellij.util.text.CharArrayUtil
import it.unimi.dsi.fastutil.ints.IntArrayList
import java.lang.StrictMath.abs
import java.lang.StrictMath.min

/**
 * Copies the logic of [com.intellij.codeInsight.daemon.impl.indentGuide.IndentGuides].
 * Needed to determine indent guide descriptors in ReSharper based IDEs, since the results are not present in [com.intellij.openapi.editor.impl.IndentsModelImpl].
 */
class DefaultIndentsDescriptors(
	private val myFile: PsiFile,
	private val myEditor: Editor,
) {
	
	private val document
		get() = myEditor.document
	
	fun buildDescriptors(): List<IndentGuideDescriptor> {
		val calculator = IndentsCalculator()
		calculator.calculate()
		val lineIndents = calculator.lineIndents
		
		val lines = IntArrayList()
		val indents = IntArrayList()
		
		lines.push(0)
		indents.push(0)
		val descriptors = ArrayList<IndentGuideDescriptor>()
		for (line in 1 until lineIndents.size) {
			ProgressManager.checkCanceled()
			val curIndent = abs(lineIndents[line])
			
			while (!indents.isEmpty && curIndent <= indents.peekInt(0)) {
				ProgressManager.checkCanceled()
				val level = indents.popInt()
				val startLine = lines.popInt()
				if (level > 0) {
					for (i in startLine until line) {
						if (level != abs(lineIndents[i])) {
							descriptors.add(createDescriptor(level, startLine, line, lineIndents))
							break
						}
					}
				}
			}
			
			val prevLine = line - 1
			val prevIndent = abs(lineIndents[prevLine])
			
			if (curIndent - prevIndent > 1) {
				lines.push(prevLine)
				indents.push(prevIndent)
			}
		}
		
		while (!indents.isEmpty) {
			ProgressManager.checkCanceled()
			val level = indents.popInt()
			val startLine = lines.popInt()
			if (level > 0) {
				descriptors.add(createDescriptor(level, startLine, document.lineCount, lineIndents))
			}
		}
		return descriptors
	}
	
	private fun createDescriptor(
		level: Int,
		startLine: Int,
		endLine: Int,
		lineIndents: IntArray,
	): IndentGuideDescriptor {
		var sLine = startLine
		while (sLine > 0 && lineIndents[sLine] < 0) sLine--
		// int codeConstructStartLine = findCodeConstructStartLine(startLine);
		return IndentGuideDescriptor(level, sLine, endLine)
	}
	
	private inner class IndentsCalculator {
		val myComments: MutableMap<String, TokenSet> = HashMap()
		val lineIndents = IntArray(document.lineCount) // negative value means the line is empty (or contains a comment) and indent
		
		// (denoted by absolute value) was deduced from enclosing non-empty lines
		val myChars = document.charsSequence
		
		/**
		 * Calculates line indents for the [target document][.myDocument].
		 */
		fun calculate() {
			val fileType = myFile.fileType
			val tabSize = EditorUtil.getTabSize(myEditor)
			
			for (line in lineIndents.indices) {
				ProgressManager.checkCanceled()
				val lineStart = document.getLineStartOffset(line)
				val lineEnd = document.getLineEndOffset(line)
				var offset = lineStart
				var column = 0
				outer@ while (offset < lineEnd) {
					when (myChars[offset]) {
						' '  -> column++
						'\t' -> column = (column / tabSize + 1) * tabSize
						else -> break@outer
					}
					offset++
				}
				// treating commented lines in the same way as empty lines
				// Blank line marker
				lineIndents[line] = if (offset == lineEnd || isComment(offset)) -1 else column
			}
			
			var topIndent = 0
			var line = 0
			while (line < lineIndents.size) {
				ProgressManager.checkCanceled()
				if (lineIndents[line] >= 0) {
					topIndent = lineIndents[line]
				}
				else {
					val startLine = line
					while (line < lineIndents.size && lineIndents[line] < 0) {
						line++
					}
					
					val bottomIndent = if (line < lineIndents.size) lineIndents[line] else topIndent
					
					var indent = min(topIndent, bottomIndent)
					if (bottomIndent < topIndent) {
						val lineStart = document.getLineStartOffset(line)
						val lineEnd = document.getLineEndOffset(line)
						val nonWhitespaceOffset = CharArrayUtil.shiftForward(myChars, lineStart, lineEnd, " \t")
						val iterator = myEditor.highlighter.createIterator(nonWhitespaceOffset)
						val tokenType = iterator.tokenType
						if (BraceMatchingUtil.isRBraceToken(iterator, myChars, fileType) ||
							tokenType != null &&
							CodeBlockSupportHandler.findMarkersRanges(myFile, tokenType.language, nonWhitespaceOffset).isNotEmpty()
						) {
							indent = topIndent
						}
					}
					
					for (blankLine in startLine until line) {
						assert(lineIndents[blankLine] == -1)
						lineIndents[blankLine] = -min(topIndent, indent)
					}
					
					
					line-- // will be incremented back at the end of the loop;
				}
				line++
			}
		}
		
		private fun isComment(offset: Int): Boolean {
			val it = myEditor.highlighter.createIterator(offset)
			val tokenType = try {
				it.tokenType
			} catch (_: Throwable) {
				return false
			}
			val language = tokenType.language
			var comments: TokenSet? = myComments[language.id]
			if (comments == null) {
				val definition = LanguageParserDefinitions.INSTANCE.forLanguage(language)
				if (definition != null) {
					comments = definition.commentTokens
				}
				if (comments == null) {
					return false
				}
				else {
					myComments[language.id] = comments
				}
			}
			return comments.contains(tokenType)
		}
	}
}
