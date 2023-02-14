package cc.moky.intellij.plugin.svga

/*******************************************************************************
 * Created on: 2023/2/7 19:09
 * Author: Moky
 * Mail: mokyue@163.com
 *******************************************************************************/

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

internal class SvgaFileEditorProvider : FileEditorProvider, DumbAware {

    /**
     * The method is expected to run fast.
     *
     * @param file file to be tested for acceptance.
     * @return `true` if provider can create valid editor for the specified `file`.
     */
    override fun accept(project: Project, file: VirtualFile): Boolean {
        return file.fileType is SvgaFileType
    }

    /**
     * Creates editor for the specified file.
     *
     *
     * This method is called only if the provider has accepted this file (i.e. method [.accept] returned
     * `true`).
     * The provider should return only valid editor.
     *
     * @return created editor for specified file.
     */
    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        return SvgaFileEditorImpl(file)
    }

    /**
     * @return editor type ID for the editors created with this FileEditorProvider. Each FileEditorProvider should have
     * a unique nonnull ID. The ID is used for saving/loading of EditorStates.
     */
    override fun getEditorTypeId(): String {
        return "svga"
    }

    /**
     * @return a policy that specifies how an editor created via this provider should be opened.
     * @see FileEditorPolicy.NONE
     *
     * @see FileEditorPolicy.HIDE_DEFAULT_EDITOR
     *
     * @see FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR
     *
     * @see FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR
     */
    override fun getPolicy(): FileEditorPolicy {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR
    }
}
