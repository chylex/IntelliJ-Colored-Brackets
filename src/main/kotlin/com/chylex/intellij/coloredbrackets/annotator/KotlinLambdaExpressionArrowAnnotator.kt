package com.chylex.intellij.coloredbrackets.annotator

import com.chylex.intellij.coloredbrackets.RainbowInfo
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import java.awt.Font

class KotlinLambdaExpressionArrowAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		if ((element as? LeafPsiElement)?.elementType == KtTokens.ARROW) {
			RainbowInfo.RAINBOW_INFO_KEY[element.parent]?.color?.let {
				holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
					.range(element as PsiElement) // Cast necessary due to overload conflict in Kotlin 2 compiler.
					.textAttributes(
						com.chylex.intellij.coloredbrackets.util.create(
							"rainbow-kotlin-arrow",
							TextAttributes(it, null, null, EffectType.BOXED, Font.PLAIN)
						)
					)
					.create()
			}
		}
	}
}
