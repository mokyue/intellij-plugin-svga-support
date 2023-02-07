package cc.moky.intellij.plugin.svga

/*******************************************************************************
 * Created on: 2023/2/7 19:08
 * Author: Moky
 * Mail: mokyue@163.com
 *******************************************************************************/

import cc.moky.intellij.plugin.utils.SvgaDataProcessor
import com.intellij.codeHighlighting.BackgroundEditorHighlighter
import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.fileEditor.FileEditorStateLevel
import com.intellij.openapi.fileEditor.impl.text.TextEditorState
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.jcef.JBCefBrowser
import java.beans.PropertyChangeListener
import javax.swing.JComponent

internal class SvgaFileEditorImpl(private val mFile: VirtualFile) : UserDataHolderBase(), FileEditor {

    companion object {

        private const val NAME = "SVGA File Editor"
    }

    override fun getComponent(): JComponent {
        val browser = JBCefBrowser()
        browser.loadHTML(SvgaDataProcessor.processSvgaData(mFile))
        return browser.component
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return null
    }

    override fun getName(): String {
        return NAME
    }

    override fun getFile(): VirtualFile {
        return mFile
    }

    override fun getState(level: FileEditorStateLevel): FileEditorState {
        return TextEditorState()
    }

    override fun setState(state: FileEditorState) {}

    override fun isModified(): Boolean {
        return false
    }

    override fun isValid(): Boolean {
        return mFile.isValid
    }

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {}

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {}

    override fun getBackgroundHighlighter(): BackgroundEditorHighlighter? {
        return null
    }

    override fun getCurrentLocation(): FileEditorLocation? {
        return null
    }

    override fun getStructureViewBuilder(): StructureViewBuilder? {
        return null
    }

    override fun dispose() {}
}
