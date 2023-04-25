package cc.moky.intellij.plugin.svga

/*******************************************************************************
 * Created on: 2023/2/7 14:52
 * Author: Moky
 * Mail: mokyue@163.com
 *******************************************************************************/

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

class SvgaFileType private constructor() : LanguageFileType(Svga.INSTANCE) {

    companion object {

        @JvmField
        val INSTANCE = SvgaFileType()
    }

    /**
     * Returns the name of the file type. The name must be unique among all file types registered in the system.
     */
    override fun getName(): String {
        return "SVGA File"
    }

    /**
     * Returns the user-readable description of the file type.
     */
    override fun getDescription(): String {
        return "SVGA animation file"
    }

    /**
     * Returns the default extension for files of the type, *not* including the leading '.'.
     */
    override fun getDefaultExtension(): String {
        return "svga"
    }

    /**
     * Returns the icon used for showing files of the type, or `null` if no icon should be shown.
     */
    override fun getIcon(): Icon {
        return SvgaIcon.FILE
    }
}
