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
import org.jetbrains.annotations.NonNls

internal class SvgaFileEditorProvider : FileEditorProvider, DumbAware {

    companion object {

        private const val EDITOR_TYPE_ID: @NonNls String = "svga"
    }

    override fun accept(project: Project, file: VirtualFile): Boolean {
        return file.fileType is SvgaFileType
    }

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        return SvgaFileEditorImpl(file)
    }

    override fun getEditorTypeId(): String {
        return EDITOR_TYPE_ID
    }

    override fun getPolicy(): FileEditorPolicy {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR
    }
}
