package com.chylex.intellij.coloredbrackets.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.SettingsCategory
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil.copyBean
import org.jetbrains.annotations.Nullable

@State(name = "ColoredBracketsSettings", storages = [Storage("colored_brackets.xml")], category = SettingsCategory.UI)
class RainbowSettings : PersistentStateComponent<RainbowSettings> {
	/**
	 * default value
	 */
	var isRainbowEnabled = true
	var isEnableRainbowRoundBrackets = true
	var isEnableRainbowSquigglyBrackets = true
	var isEnableRainbowSquareBrackets = true
	var isEnableRainbowAngleBrackets = true
	var isShowRainbowIndentGuides = true
	var isDoNOTRainbowifyBracketsWithoutContent = false
	var isDoNOTRainbowifyTheFirstLevel = false
	var isRainbowifyHTMLInsideJS = true
	var isRainbowifyKotlinLabel = true
	var isRainbowifyKotlinFunctionLiteralBracesAndArrow = true
	var isOverrideMatchedBraceAttributes = true
	var pressAnyKeyToRemoveTheHighlightingEffects = false
	var applyColorsOfRoundForAllBrackets = false
	
	//https://github.com/izhangzhihao/intellij-rainbow-brackets/issues/391
	var cycleCountOnAllBrackets = false
	var numberOfColors = 5
	
	var disableRainbowIndentsInZenMode = true
	var useColorGenerator = false
	var customColorGeneratorOption: String? = null
	var rainbowifyTagNameInXML = false
	var doNOTRainbowifyTemplateString = false
	var doNOTRainbowifyBigFiles = true
	var bigFilesLinesThreshold = 100_000
	
	var languageBlacklist: Set<String> = setOf("hocon", "mxml")
	
	var suppressDisabledCheck = false
	var suppressBigFileCheck = false
	var suppressBlackListCheck = false
	var rainbowifyPythonKeywords = false
	
	@Nullable
	override fun getState() = this
	
	override fun loadState(state: RainbowSettings) {
		copyBean(state, this)
	}
	
	companion object {
		val instance: RainbowSettings
			get() = ApplicationManager.getApplication().getService(RainbowSettings::class.java)
	}
}
