package cc.moky.intellij.plugin.svga

/*******************************************************************************
 * Created on: 2026/5/12 09:43
 * Author: Moky
 * Mail: mokyue@163.com
 *******************************************************************************/

import com.intellij.ide.ui.UISettings
import com.intellij.util.ui.JBFont
import java.awt.Font

object FontUtil {

    fun getEffectiveUIFont(): Font {
        val settings = UISettings.getInstance()
        return if (!settings.fontFace.isNullOrBlank()) {
            Font(settings.fontFace, Font.PLAIN, settings.fontSize)
        } else {
            JBFont.label()
        }
    }

    fun getEffectiveUIFont(size: Float): Font {
        return getEffectiveUIFont().deriveFont(size)
    }

    fun getEffectiveUIFontBold(): Font {
        return getEffectiveUIFont().deriveFont(Font.BOLD)
    }
}
