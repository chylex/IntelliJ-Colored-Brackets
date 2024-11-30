package com.chylex.intellij.coloredbrackets

import com.chylex.intellij.coloredbrackets.settings.RainbowConfigurable
import com.chylex.intellij.coloredbrackets.settings.RainbowSettings
import com.chylex.intellij.coloredbrackets.util.memoizedFileExtension
import com.chylex.intellij.coloredbrackets.util.toPsiFile
import com.chylex.intellij.coloredbrackets.visitor.RainbowHighlightVisitor.Companion.checkForBigFile
import com.intellij.icons.AllIcons
import com.intellij.ide.actions.ShowSettingsUtilImpl
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.Ref
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications
import com.intellij.ui.HyperlinkLabel

class RainbowifyBanner : EditorNotifications.Provider<EditorNotificationPanel>() {
	override fun getKey(): Key<EditorNotificationPanel> = KEY
	
	override fun createNotificationPanel(file: VirtualFile, fileEditor: FileEditor, project: Project): EditorNotificationPanel? {
		
		if (!RainbowSettings.instance.isRainbowEnabled) {
			if (RainbowSettings.instance.suppressDisabledCheck) return null
			return EditorNotificationPanel().apply {
				text("Colored Brackets is now disabled")
				icon(AllIcons.General.GearPlain)
				createComponentActionLabel("got it, don't show again") {
					RainbowSettings.instance.suppressDisabledCheck = true
					EditorNotifications.getInstance(project).updateAllNotifications()
				}
				
				createComponentActionLabel("enable Colored Brackets") {
					RainbowSettings.instance.isRainbowEnabled = true
					EditorNotifications.getInstance(project).updateAllNotifications()
				}
			}
		}
		
		val psiFile = file.toPsiFile(project)
		if (psiFile != null && !checkForBigFile(psiFile)) {
			if (RainbowSettings.instance.suppressBigFileCheck) return null
			return EditorNotificationPanel().apply {
				text("Rainbowify is disabled for files > " + RainbowSettings.instance.bigFilesLinesThreshold + " lines")
				icon(AllIcons.General.InspectionsEye)
				createComponentActionLabel("got it, don't show again") {
					RainbowSettings.instance.suppressBigFileCheck = true
					EditorNotifications.getInstance(project).updateAllNotifications()
				}
				
				createComponentActionLabel("open settings") {
					ShowSettingsUtilImpl.showSettingsDialog(project, RainbowConfigurable.ID, "")
					EditorNotifications.getInstance(project).updateAllNotifications()
				}
			}
		}
		
		if (
			RainbowSettings.instance.languageBlacklist.contains(file.fileType.name) ||
			RainbowSettings.instance.languageBlacklist.contains(memoizedFileExtension(file.name))
		) {
			if (RainbowSettings.instance.suppressBlackListCheck) return null
			return EditorNotificationPanel().apply {
				text("Rainbowify is disabled because the language/file extension is in the black list")
				icon(AllIcons.General.InspectionsEye)
				
				createComponentActionLabel("got it, don't show again") {
					RainbowSettings.instance.suppressBlackListCheck = true
					EditorNotifications.getInstance(project).updateAllNotifications()
				}
				
				createComponentActionLabel("open setting") {
					ShowSettingsUtilImpl.showSettingsDialog(project, RainbowConfigurable.ID, "")
					EditorNotifications.getInstance(project).updateAllNotifications()
				}
			}
		}
		
		return null
	}
	
	companion object {
		private val KEY = Key.create<EditorNotificationPanel>("RainbowifyBanner")
		
		fun EditorNotificationPanel.createComponentActionLabel(labelText: String, callback: (HyperlinkLabel) -> Unit) {
			val label: Ref<HyperlinkLabel> = Ref.create()
			label.set(createActionLabel(labelText) {
				callback(label.get())
			})
		}
	}
}
