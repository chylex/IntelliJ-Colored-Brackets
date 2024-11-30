package com.chylex.intellij.coloredbrackets

import com.chylex.intellij.coloredbrackets.provider.BracePairProvider
import com.chylex.intellij.coloredbrackets.util.memoize
import com.intellij.codeInsight.highlighting.BraceMatchingUtil
import com.intellij.lang.BracePair
import com.intellij.lang.CompositeLanguage
import com.intellij.lang.Language
import com.intellij.lang.LanguageBraceMatching
import com.intellij.lang.LanguageExtension
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.tree.IElementType

object BracePairs {
	
	private val providers = LanguageExtension<BracePairProvider>("com.chylex.coloredbrackets.bracePairProvider")
	
	private val bracePairs =
		Language.getRegisteredLanguages()
			.map { language ->
				if (language is CompositeLanguage) {
					return@map language.displayName to null
				}
				
				val pairs =
					LanguageBraceMatching.INSTANCE.forLanguage(language)?.pairs.let {
						if (it.isNullOrEmpty()) {
							language.associatedFileType
								?.let { fileType -> BraceMatchingUtil.getBraceMatcher(fileType, language) as? PairedBraceMatcher }
								?.pairs
						}
						else {
							it
						}
					}
				
				val pairsList = providers.forLanguage(language)?.pairs()?.let {
					if (!pairs.isNullOrEmpty()) {
						it.toMutableSet().apply { addAll(pairs) }
					}
					else {
						it
					}
				} ?: pairs?.toList()
				
				val braceMap: MutableMap<String, MutableList<BracePair>> = mutableMapOf()
				
				val blackSet = providers.forLanguage(language)?.blackList()?.map { it.toString() }?.toSet()
				
				pairsList
					?.filter {
						if (blackSet != null) {
							!blackSet.contains(it.toString())
						}
						else {
							true
						}
					}
					?.map { listOf(Pair(it.leftBraceType.toString(), it), Pair(it.rightBraceType.toString(), it)) }
					?.flatten()
					?.forEach {
						val bracePairs = braceMap[it.first]
						if (bracePairs == null) {
							braceMap[it.first] = mutableListOf(it.second)
						}
						else {
							bracePairs.add(it.second)
						}
					}
				
				language.displayName to braceMap
			}
			.toMap()
	
	fun getBracePairs(language: Language): MutableMap<String, MutableList<BracePair>>? = bracePairs[language.displayName]
	
	private fun getBraceTypeSetOf(language: Language): Set<IElementType> = getBracePairs(language)?.values?.flatten()?.map { listOf(it.leftBraceType, it.rightBraceType) }?.flatten()?.toSet() ?: emptySet()
	
	val braceTypeSet: (Language) -> Set<IElementType> = { language: Language -> getBraceTypeSetOf(language) }.memoize()
}

inline val Language.bracePairs: MutableMap<String, MutableList<BracePair>>?
	get() = BracePairs.getBracePairs(this)

inline val Language.braceTypeSet: Set<IElementType>
	get() = BracePairs.braceTypeSet(this)
