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

/**
 * FileEditor implementation for SVGA animation files.
 * Uses JBCefBrowser to render SVGA animations in the IDE.
 *
 * Note: The browser instance is cached to prevent memory leaks.
 * IntelliJ may call getComponent() multiple times (e.g., on tab switch),
 * and creating a new JBCefBrowser each time would cause memory leaks.
 */
internal class SvgaFileEditorImpl(private val mFile: VirtualFile) : UserDataHolderBase(), FileEditor {

    companion object {
        private const val NAME = "SVGA File Editor"
    }

    /**
     * Cached JBCefBrowser instance to avoid memory leaks.
     * Lazily initialized on first getComponent() call and reused for subsequent calls.
     */
    private var browser: JBCefBrowser? = null

    /**
     * Cached component reference for the browser.
     * Initialized along with the browser instance.
     */
    private var browserComponent: JComponent? = null

    override fun getComponent(): JComponent {
        // Return cached component if already initialized
        browserComponent?.let { return it }

        // Create and cache browser instance on first call
        val newBrowser = JBCefBrowser()
        newBrowser.loadHTML(SvgaDataProcessor.processSvgaData(mFile))
        browser = newBrowser
        browserComponent = newBrowser.component

        return newBrowser.component
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return browserComponent
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

    /**
     * Disposes the editor and releases all associated resources.
     * This includes disposing the JBCefBrowser instance to free native resources.
     */
    override fun dispose() {
        // Dispose browser to release native JCEF resources
        browser?.dispose()
        browser = null
        browserComponent = null
    }
}
