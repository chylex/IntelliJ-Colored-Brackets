package com.chylex.intellij.coloredbrackets.provider

import com.intellij.lang.BracePair
import com.jetbrains.rider.ideaInterop.fileTypes.csharp.lexer.CSharpTokenType.GT
import com.jetbrains.rider.ideaInterop.fileTypes.csharp.lexer.CSharpTokenType.LBRACE
import com.jetbrains.rider.ideaInterop.fileTypes.csharp.lexer.CSharpTokenType.LBRACKET
import com.jetbrains.rider.ideaInterop.fileTypes.csharp.lexer.CSharpTokenType.LT
import com.jetbrains.rider.ideaInterop.fileTypes.csharp.lexer.CSharpTokenType.RBRACE
import com.jetbrains.rider.ideaInterop.fileTypes.csharp.lexer.CSharpTokenType.RBRACKET

class CSharpBracePairProvider : BracePairProvider {
	override fun pairs(): List<BracePair> = listOf(
		//BracePair(LPARENTH, RPARENTH, false),
		BracePair(LBRACE, RBRACE, false),
		BracePair(LBRACKET, RBRACKET, false),
		BracePair(LT, GT, false),
	)
}
