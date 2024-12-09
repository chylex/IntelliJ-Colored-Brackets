package com.chylex.intellij.coloredbrackets.indents

import com.intellij.codeHighlighting.Pass
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactory
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactoryRegistrar
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.impl.ImaginaryEditor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class RainbowIndentsPassFactory :
	TextEditorHighlightingPassFactoryRegistrar, TextEditorHighlightingPassFactory {
	
	override fun createHighlightingPass(file: PsiFile, editor: Editor): RainbowIndentsPass? {
		return when (editor) {
			is ImaginaryEditor -> null
			else               -> RainbowIndentsPass(file.project, editor, file)
		}
	}
	
	override fun registerHighlightingPassFactory(registrar: TextEditorHighlightingPassRegistrar, project: Project) {
		registrar.registerTextEditorHighlightingPass(
			this,
			TextEditorHighlightingPassRegistrar.Anchor.LAST,
			Pass.LAST_PASS,
			false,
			false
		)
	}
}
