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
import com.intellij.openapi.util.Ref
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationProvider
import com.intellij.ui.EditorNotifications
import com.intellij.ui.HyperlinkLabel
import java.util.function.Function
import javax.swing.JComponent

class RainbowifyBanner : EditorNotificationProvider {
	override fun collectNotificationData(project: Project, file: VirtualFile): Function<in FileEditor, out JComponent?> {
		return Function { createNotificationPanel(project, file) }
	}
	
	private fun createNotificationPanel(project: Project, file: VirtualFile): EditorNotificationPanel? {
		val settings = RainbowSettings.instance
		
		if (!settings.isRainbowEnabled) {
			if (settings.suppressDisabledCheck) {
				return null
			}
			return EditorNotificationPanel().apply {
				text("Colored Brackets is now disabled")
				icon(AllIcons.General.GearPlain)
				createComponentActionLabel("got it, don't show again") {
					settings.suppressDisabledCheck = true
					EditorNotifications.getInstance(project).updateAllNotifications()
				}
				
				createComponentActionLabel("enable Colored Brackets") {
					settings.isRainbowEnabled = true
					EditorNotifications.getInstance(project).updateAllNotifications()
				}
			}
		}
		
		val psiFile = file.toPsiFile(project)
		if (psiFile != null && !checkForBigFile(psiFile)) {
			if (settings.suppressBigFileCheck) {
				return null
			}
			return EditorNotificationPanel().apply {
				text("Rainbowify is disabled for files > " + settings.bigFilesLinesThreshold + " lines")
				icon(AllIcons.General.InspectionsEye)
				createComponentActionLabel("got it, don't show again") {
					settings.suppressBigFileCheck = true
					EditorNotifications.getInstance(project).updateAllNotifications()
				}
				
				createComponentActionLabel("open settings") {
					ShowSettingsUtilImpl.showSettingsDialog(project, RainbowConfigurable.ID, "")
					EditorNotifications.getInstance(project).updateAllNotifications()
				}
			}
		}
		
		if (
			settings.languageBlacklist.contains(file.fileType.name) ||
			settings.languageBlacklist.contains(memoizedFileExtension(file.name))
		) {
			if (settings.suppressBlackListCheck) {
				return null
			}
			return EditorNotificationPanel().apply {
				text("Rainbowify is disabled because the language/file extension is in the black list")
				icon(AllIcons.General.InspectionsEye)
				
				createComponentActionLabel("got it, don't show again") {
					settings.suppressBlackListCheck = true
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
	
	private fun EditorNotificationPanel.createComponentActionLabel(labelText: String, callback: (HyperlinkLabel) -> Unit) {
		val label: Ref<HyperlinkLabel> = Ref.create()
		label.set(createActionLabel(labelText) {
			callback(label.get())
		})
	}
}
