package com.github.izhangzhihao.rainbow.brackets.visitor

import com.github.izhangzhihao.rainbow.brackets.settings.RainbowSettings
import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.jetbrains.python.PyTokenTypes.BREAK_KEYWORD
import com.jetbrains.python.PyTokenTypes.CLASS_KEYWORD
import com.jetbrains.python.PyTokenTypes.CONTINUE_KEYWORD
import com.jetbrains.python.PyTokenTypes.DEF_KEYWORD
import com.jetbrains.python.PyTokenTypes.ELIF_KEYWORD
import com.jetbrains.python.PyTokenTypes.ELSE_KEYWORD
import com.jetbrains.python.PyTokenTypes.EXCEPT_KEYWORD
import com.jetbrains.python.PyTokenTypes.FINALLY_KEYWORD
import com.jetbrains.python.PyTokenTypes.FOR_KEYWORD
import com.jetbrains.python.PyTokenTypes.IF_KEYWORD
import com.jetbrains.python.PyTokenTypes.RAISE_KEYWORD
import com.jetbrains.python.PyTokenTypes.RETURN_KEYWORD
import com.jetbrains.python.PyTokenTypes.TRY_KEYWORD
import com.jetbrains.python.PyTokenTypes.WHILE_KEYWORD
import com.jetbrains.python.PyTokenTypes.WITH_KEYWORD
import com.jetbrains.python.PyTokenTypes.YIELD_KEYWORD
import com.jetbrains.python.psi.PyStatement


class PythonRainbowVisitor : RainbowHighlightVisitor() {

    override fun suitableForFile(file: PsiFile)
            : Boolean = super.suitableForFile(file) &&
            RainbowSettings.instance.rainbowifyPythonKeywords &&
            (file.language.id == "Python" ||
                    file.viewProvider.allFiles.any { it.language.id == "Python" }
                    )

    override fun clone(): HighlightVisitor = PythonRainbowVisitor()

    override fun visit(element: PsiElement) {
        val type = (element as? LeafPsiElement)?.elementType ?: return
        val exists = statementKeywords.contains(type)
        if (exists) {
            val level = element.getBracketLevel()
            if (RainbowSettings.instance.isDoNOTRainbowifyTheFirstLevel) {
                if (level >= 1) {
                    rainbowPairs(element, level)
                }
            } else {
                if (level >= 0) {
                    rainbowPairs(element, level)
                }
            }
        }
    }

    private fun rainbowPairs(element: LeafPsiElement, level: Int) {
        element.setHighlightInfo(element.parent, level, element, element)
    }

    companion object {
        val statementKeywords = setOf(
            IF_KEYWORD,
            ELSE_KEYWORD,
            ELIF_KEYWORD,
            BREAK_KEYWORD,
            CONTINUE_KEYWORD,
            CLASS_KEYWORD,
            DEF_KEYWORD,
            EXCEPT_KEYWORD,
            FINALLY_KEYWORD,
            FOR_KEYWORD,
            RAISE_KEYWORD,
            RETURN_KEYWORD,
            TRY_KEYWORD,
            WITH_KEYWORD,
            WHILE_KEYWORD,
            YIELD_KEYWORD,
        )

        private fun LeafPsiElement.getBracketLevel(): Int =
            iterateBracketParents(this, -1)

        private tailrec fun iterateBracketParents(
            element: PsiElement?,
            count: Int
        ): Int {
            if (element == null || element is PsiFile) {
                return count
            }

            var nextCount = count

            if (element is PyStatement) {
                nextCount++
            }

            return iterateBracketParents(
                element.parent,
                nextCount,
            )
        }
    }
}
