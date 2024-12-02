package com.chylex.intellij.coloredbrackets.visitor

import com.chylex.intellij.coloredbrackets.settings.RainbowSettings
import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.jetbrains.rider.languages.fileTypes.csharp.kotoparser.lexer.CSharpTokenType
import com.jetbrains.rider.languages.fileTypes.csharp.psi.CSharpDummyNode

class CSharpRainbowVisitor : ReSharperRainbowVisitor("C#") {
	
	override fun suitableForFile(file: PsiFile): Boolean {
		return super.suitableForFile(file) && RainbowSettings.instance.isEnableRainbowAngleBrackets
	}
	
	override fun isAllowedElementType(type: IElementType): Boolean {
		return type == CSharpTokenType.LPARENTH || type == CSharpTokenType.RPARENTH
	}
	
	override fun PsiElement.isDummyNode(): Boolean {
		return this is CSharpDummyNode
	}
	
	override fun clone(): HighlightVisitor = CSharpRainbowVisitor()
}
