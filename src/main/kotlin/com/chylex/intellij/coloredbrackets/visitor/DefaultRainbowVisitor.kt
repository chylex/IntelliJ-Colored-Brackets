package com.chylex.intellij.coloredbrackets.visitor

import com.chylex.intellij.coloredbrackets.bracePairs
import com.chylex.intellij.coloredbrackets.braceTypeSet
import com.chylex.intellij.coloredbrackets.settings.RainbowSettings
import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.lang.BracePair
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType

class DefaultRainbowVisitor : RainbowHighlightVisitor() {
	
	override fun clone(): HighlightVisitor = DefaultRainbowVisitor()
	
	override fun visit(element: PsiElement) {
		val type = (element as? LeafPsiElement)?.elementType ?: return
		
		val settings = RainbowSettings.instance
		val processor = Processor(settings)
		val matching = processor.filterPairs(type, element) ?: return
		
		val pair =
			if (matching.size == 1) {
				matching[0]
			}
			else {
				matching.find { processor.isValidBracket(element, it) }
			} ?: return
		
		val level = processor.getBracketLevel(element, pair)
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
	
	private class Processor(private val settings: RainbowSettings) {
		
		fun getBracketLevel(element: LeafPsiElement, pair: BracePair): Int = iterateBracketParents(element.parent, pair, -1)
		
		private tailrec fun iterateBracketParents(element: PsiElement?, pair: BracePair, count: Int): Int {
			if (element == null || element is PsiFile) {
				return count
			}
			
			var nextCount = count
			if (!settings.cycleCountOnAllBrackets) {
				if (element.haveBrackets(
						{ it.elementType() == pair.leftBraceType },
						{ it.elementType() == pair.rightBraceType })
				) {
					nextCount++
				}
			}
			else {
				if (element.haveBrackets(
						{ element.language.braceTypeSet.contains(it.elementType()) },
						{ element.language.braceTypeSet.contains(it.elementType()) })
				) {
					nextCount++
				}
			}
			
			return iterateBracketParents(element.parent, pair, nextCount)
		}
		
		private inline fun PsiElement.haveBrackets(
			checkLeft: (PsiElement) -> Boolean,
			checkRight: (PsiElement) -> Boolean,
		): Boolean {
			if (this is LeafPsiElement) {
				return false
			}
			
			var findLeftBracket = false
			var findRightBracket = false
			var left: PsiElement? = firstChild
			var right: PsiElement? = lastChild
			while (left != right && (!findLeftBracket || !findRightBracket)) {
				val needBreak = left == null || left.nextSibling == right
				
				if (left is LeafPsiElement && checkLeft(left)) {
					findLeftBracket = true
				}
				else {
					left = left?.nextSibling
				}
				if (right is LeafPsiElement && checkRight(right)) {
					findRightBracket = true
				}
				else {
					right = right?.prevSibling
				}
				
				if (needBreak) {
					break
				}
			}
			
			//For https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/830
			if (settings.doNOTRainbowifyTemplateString) {
				if (left?.prevSibling?.textMatches("$") == true) return false
			}
			
			return findLeftBracket && findRightBracket
		}
		
		private fun PsiElement.elementType(): IElementType? {
			return (this as? LeafPsiElement)?.elementType
		}
		
		fun isValidBracket(element: LeafPsiElement, pair: BracePair): Boolean {
			val pairType = when (element.elementType) {
				pair.leftBraceType  -> pair.rightBraceType
				pair.rightBraceType -> pair.leftBraceType
				else                -> return false
			}
			
			return if (pairType == pair.leftBraceType) {
				checkBracePair(element, element.parent.firstChild, pairType, PsiElement::getNextSibling)
			}
			else {
				checkBracePair(element, element.parent.lastChild, pairType, PsiElement::getPrevSibling)
			}
		}
		
		private fun checkBracePair(
			brace: PsiElement,
			start: PsiElement,
			type: IElementType,
			next: PsiElement.() -> PsiElement?,
		): Boolean {
			var element: PsiElement? = start
			while (element != null && element != brace) {
				if (element is LeafPsiElement && element.elementType == type) {
					return true
				}
				
				element = element.next()
			}
			
			return false
		}
		
		fun filterPairs(type: IElementType, element: LeafPsiElement): List<BracePair>? {
			val pairs = element.language.bracePairs ?: return null
			val filterBraceType = pairs[type.toString()]
			return when {
				filterBraceType.isNullOrEmpty()                             -> {
					null
				}
				// https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/198
				element.javaClass.simpleName == "OCMacroForeignLeafElement" -> {
					null
				}
				
				settings.isDoNOTRainbowifyBracketsWithoutContent            -> {
					filterBraceType
						.filterNot { it.leftBraceType == type && element.nextSibling?.elementType() == it.rightBraceType }
						.filterNot { it.rightBraceType == type && element.prevSibling?.elementType() == it.leftBraceType }
				}
				
				else                                                        -> {
					filterBraceType
				}
			}
		}
	}
}
