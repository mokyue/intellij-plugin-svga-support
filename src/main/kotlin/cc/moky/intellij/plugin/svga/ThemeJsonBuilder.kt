package cc.moky.intellij.plugin.svga

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.JBColor

internal object ThemeJsonBuilder {

    fun build(): String {
        val border = JBColor.border()
        val bg = JBColor.background()
        val font = deriveColor(JBColor.foreground(), 60)
        val fontFamily = FontUtil.getEffectiveUIFont().family
        val fontFamilyMono = EditorColorsManager.getInstance().globalScheme.editorFontName
        val tabActiveBg = deriveColor(bg, 15)
        val hoverBg = deriveColor(bg, 25)
        val scrollbarThumbColor = deriveColor(bg, 40)
        val scrollbarThumbHoverColor = deriveColor(bg, 60)
        val rowAltBg = deriveColor(bg, 6)
        return buildString {
            append("{")
            append("\"borderColor\":\"rgb(${border.red},${border.green},${border.blue})\",")
            append("\"backgroundColor\":\"rgb(${bg.red},${bg.green},${bg.blue})\",")
            append("\"fontColor\":\"rgb(${font.red},${font.green},${font.blue})\",")
            append("\"fontFamily\":\"$fontFamily\",")
            append("\"fontFamilyMono\":\"$fontFamilyMono\",")
            append("\"tabActiveBg\":\"rgb(${tabActiveBg.red},${tabActiveBg.green},${tabActiveBg.blue})\",")
            append("\"hoverBg\":\"rgb(${hoverBg.red},${hoverBg.green},${hoverBg.blue})\",")
            append("\"scrollbarThumbColor\":\"rgb(${scrollbarThumbColor.red},${scrollbarThumbColor.green},${scrollbarThumbColor.blue})\",")
            append("\"scrollbarThumbHoverColor\":\"rgb(${scrollbarThumbHoverColor.red},${scrollbarThumbHoverColor.green},${scrollbarThumbHoverColor.blue})\",")
            append("\"rowAltBg\":\"rgb(${rowAltBg.red},${rowAltBg.green},${rowAltBg.blue})\"")
            append("}")
        }
    }

    private fun deriveColor(color: java.awt.Color, offset: Int): java.awt.Color {
        val brightness = (color.red + color.green + color.blue) / 3
        val r = if (brightness < 128) minOf(color.red + offset, 255) else maxOf(color.red - offset, 0)
        val g = if (brightness < 128) minOf(color.green + offset, 255) else maxOf(color.green - offset, 0)
        val b = if (brightness < 128) minOf(color.blue + offset, 255) else maxOf(color.blue - offset, 0)
        return java.awt.Color(r, g, b)
    }
}
