package com.chylex.intellij.coloredbrackets.visitor

import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.jetbrains.rider.cpp.fileType.lexer.CppTokenTypes
import com.jetbrains.rider.cpp.fileType.psi.CppDummyNode

class CppRainbowVisitor : ReSharperRainbowVisitor("C++") {
	
	override fun clone(): HighlightVisitor = CppRainbowVisitor()
	
	override fun isAllowedElementType(type: IElementType): Boolean {
		return type == CppTokenTypes.LPAR || type == CppTokenTypes.RPAR
	}
	
	override fun PsiElement.isDummyNode(): Boolean {
		return this is CppDummyNode
	}
}
