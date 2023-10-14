package com.github.izhangzhihao.rainbow.brackets.provider

import com.goide.template.GoTemplateTypes.LDOUBLE_BRACE
import com.goide.template.GoTemplateTypes.LPAREN
import com.goide.template.GoTemplateTypes.RDOUBLE_BRACE
import com.goide.template.GoTemplateTypes.RPAREN
import com.intellij.lang.BracePair

class GoTemplateProvider : BracePairProvider {
    override fun pairs(): List<BracePair> = listOf(
            BracePair(LDOUBLE_BRACE, RDOUBLE_BRACE, true),
            BracePair(LPAREN, RPAREN, true)
    )
}
