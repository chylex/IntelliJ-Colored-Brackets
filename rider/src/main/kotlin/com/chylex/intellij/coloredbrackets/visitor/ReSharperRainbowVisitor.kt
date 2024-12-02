package com.chylex.intellij.coloredbrackets.visitor

import com.chylex.intellij.coloredbrackets.bracePairs
import com.chylex.intellij.coloredbrackets.settings.RainbowSettings
import com.intellij.lang.BracePair
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType

abstract class ReSharperRainbowVisitor(private val languageId: String) : RainbowHighlightVisitor() {
	
	override fun suitableForFile(file: PsiFile): Boolean {
		return super.suitableForFile(file) && (file.language.id == languageId || file.viewProvider.allFiles.any { it.language.id == languageId })
	}
	
	protected abstract fun isAllowedElementType(type: IElementType): Boolean
	
	final override fun visit(element: PsiElement) {
		val type = (element as? LeafPsiElement)?.elementType?.takeIf(::isAllowedElementType) ?: return
		val pair = type.language.bracePairs?.getValue(type.toString())?.singleOrNull() ?: return
		
		val settings = RainbowSettings.instance
		
		if (settings.isDoNOTRainbowifyBracketsWithoutContent) {
			if (pair.leftBraceType == type && element.nextSibling?.elementType == pair.rightBraceType) {
				return
			}
			
			if (pair.rightBraceType == type && element.prevSibling?.elementType == pair.leftBraceType) {
				return
			}
		}
		
		val level = element.getBracketLevel(pair, type)
		
		if (settings.isDoNOTRainbowifyTheFirstLevel) {
			if (level >= 1) {
				rainbowPairs(element, pair, level)
			}
		}
		else {
			if (level >= 0) {
				rainbowPairs(element, pair, level)
			}
		}
	}
	
	private fun rainbowPairs(element: LeafPsiElement, pair: BracePair, level: Int) {
		val startElement = element.takeIf { it.elementType == pair.leftBraceType }
		val endElement = element.takeIf { it.elementType == pair.rightBraceType }
		element.setHighlightInfo(element.parent, level, startElement, endElement)
	}
	
	private fun LeafPsiElement.getBracketLevel(pair: BracePair, type: IElementType): Int {
		return iterateBracketParents(this, pair, -1, type)
	}
	
	private tailrec fun iterateBracketParents(element: PsiElement?, pair: BracePair, count: Int, type: IElementType): Int {
		if (element == null || element is PsiFile) {
			return count
		}
		
		var nextCount = count
		
		if (element is LeafPsiElement) {
			if (type == pair.leftBraceType && element.elementType == pair.rightBraceType) {
				nextCount--
			}
			
			if (type == pair.rightBraceType && element.elementType == pair.leftBraceType) {
				nextCount--
			}
			
			if (type == element.elementType) {
				nextCount++
			}
		}
		
		return if (type == pair.leftBraceType) {
			val prev = element.prevSibling
			if (prev == null) {
				iterateBracketParents(element.parent.prevSibling.iterForPreDummyNode()?.lastChild, pair, nextCount, type)
			}
			else {
				iterateBracketParents(prev, pair, nextCount, type)
			}
		}
		else {
			val next = element.nextSibling
			if (next == null) {
				iterateBracketParents(element.parent.nextSibling.iterForNextDummyNode()?.firstChild, pair, nextCount, type)
			}
			else {
				iterateBracketParents(next, pair, nextCount, type)
			}
		}
	}
	
	protected abstract fun PsiElement.isDummyNode(): Boolean
	
	private tailrec fun PsiElement?.iterForNextDummyNode(): PsiElement? {
		return when {
			this == null             -> null
			this.isDummyNode()       -> this
			this.nextSibling == null -> null
			else                     -> this.nextSibling.iterForNextDummyNode()
		}
	}
	
	private tailrec fun PsiElement?.iterForPreDummyNode(): PsiElement? {
		return when {
			this == null             -> null
			this.isDummyNode()       -> this
			this.prevSibling == null -> null
			else                     -> this.prevSibling.iterForPreDummyNode()
		}
	}
}
