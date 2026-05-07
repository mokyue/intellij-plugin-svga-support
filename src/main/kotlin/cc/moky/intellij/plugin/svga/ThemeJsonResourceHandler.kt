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
        val fontFamily = UIUtil.getLabelFont().family.escapeJson()
        return """{"borderColor":"rgb(${border.red},${border.green},${border.blue})","backgroundColor":"rgb(${bg.red},${bg.green},${bg.blue})","fontColor":"rgb(${font.red},${font.green},${font.blue})","fontFamily":"$fontFamily"}"""
    }

    private fun String.escapeJson(): String {
        return replace("\\", "\\\\").replace("\"", "\\\"")
    }
}
