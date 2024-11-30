package com.chylex.intellij.coloredbrackets.provider

import com.intellij.lang.BracePair
import com.jetbrains.cidr.lang.parser.OCTokenTypes

class OCBracePairProvider : BracePairProvider {
	override fun pairs(): List<BracePair> = listOf(BracePair(OCTokenTypes.LT, OCTokenTypes.GT, false))
}
