package com.chylex.intellij.coloredbrackets.provider

import com.intellij.lang.BracePair
import com.jetbrains.cidr.lang.parser.OCLexerTokenTypes

class OCBracePairProvider : BracePairProvider {
	override fun pairs(): List<BracePair> = listOf(BracePair(OCLexerTokenTypes.LT, OCLexerTokenTypes.GT, false))
}
