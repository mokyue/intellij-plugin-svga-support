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

    override fun getName(): String {
        return "SVGA File"
    }

    override fun getDescription(): String {
        return "SVGA animation file"
    }

    override fun getDefaultExtension(): String {
        return "svga"
    }

    override fun getIcon(): Icon {
        return SvgaIcon.FILE
    }
}
