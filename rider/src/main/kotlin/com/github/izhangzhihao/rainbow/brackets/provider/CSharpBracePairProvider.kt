package com.github.izhangzhihao.rainbow.brackets.provider

import com.intellij.lang.BracePair
import com.jetbrains.rider.languages.fileTypes.csharp.kotoparser.lexer.CSharpTokenType

class CSharpBracePairProvider : BracePairProvider {
    override fun pairs(): List<BracePair> = listOf(
            BracePair(CSharpTokenType.LPARENTH, CSharpTokenType.RPARENTH, false),
            BracePair(CSharpTokenType.LBRACE, CSharpTokenType.RBRACE, false),
            BracePair(CSharpTokenType.LBRACKET, CSharpTokenType.RBRACKET, false),
            BracePair(CSharpTokenType.LT, CSharpTokenType.GT, false),
    )
}
