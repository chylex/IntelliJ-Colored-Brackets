package com.chylex.intellij.coloredbrackets.provider

import com.intellij.lang.BracePair
import com.jetbrains.rider.cpp.fileType.lexer.CppTokenTypes

class CppBracePairProvider : BracePairProvider {
	override fun pairs(): List<BracePair> = listOf(
		BracePair(CppTokenTypes.LPAR, CppTokenTypes.RPAR, false),
		BracePair(CppTokenTypes.LBRACE, CppTokenTypes.RBRACE, false),
		BracePair(CppTokenTypes.LBRACKET, CppTokenTypes.RBRACKET, false),
		BracePair(CppTokenTypes.LT, CppTokenTypes.GT, false),
	)
}
