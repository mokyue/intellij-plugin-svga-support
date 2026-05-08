package cc.moky.intellij.plugin.svga

import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefResourceHandler
import org.cef.handler.CefResourceRequestHandlerAdapter
import org.cef.network.CefRequest
import java.io.InputStream

internal class ClasspathResourceHandler(
    private val resourcePath: String,
    private val mimeType: String
) : CefResourceRequestHandlerAdapter() {

    override fun getResourceHandler(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?
    ): CefResourceHandler? {
        val stream: InputStream = javaClass.classLoader.getResourceAsStream(resourcePath) ?: return null
        return StreamCefResourceHandler(stream, mimeType)
    }
}
