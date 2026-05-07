package cc.moky.intellij.plugin.svga

import com.intellij.ui.JBColor
import com.intellij.util.ui.UIUtil
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefResourceHandler
import org.cef.handler.CefResourceRequestHandlerAdapter
import org.cef.network.CefRequest
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

internal class ThemeJsonResourceHandler : CefResourceRequestHandlerAdapter() {

    override fun getResourceHandler(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?
    ): CefResourceHandler {
        val json = buildThemeJson()
        val bytes = json.toByteArray(StandardCharsets.UTF_8)
        val stream = ByteArrayInputStream(bytes)
        return StreamCefResourceHandler(stream, "application/json", bytes.size)
    }

    private fun buildThemeJson(): String {
        val border = JBColor.border()
        val bg = JBColor.background()
        val font = JBColor.foreground()
        val fontFamily = escapeJson(UIUtil.getLabelFont().family)
        return buildString {
            append("{")
            append("\"borderColor\":\"rgb(${border.red},${border.green},${border.blue})\",")
            append("\"backgroundColor\":\"rgb(${bg.red},${bg.green},${bg.blue})\",")
            append("\"fontColor\":\"rgb(${font.red},${font.green},${font.blue})\",")
            append("\"fontFamily\":\"$fontFamily\"")
            append("}")
        }
    }

    private fun escapeJson(value: String): String {
        val sb = StringBuilder(value.length)
        for (ch in value) {
            when (ch) {
                '"' -> sb.append("\\\"")
                '\\' -> sb.append("\\\\")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                else -> if (ch.code < 0x20) sb.append("\\u${ch.code.toString(16).padStart(4, '0')}") else sb.append(ch)
            }
        }
        return sb.toString()
    }
}
