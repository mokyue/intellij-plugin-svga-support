package cc.moky.intellij.plugin.svga

import com.intellij.openapi.vfs.VirtualFile
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefRequestHandlerAdapter
import org.cef.handler.CefResourceRequestHandler
import org.cef.misc.BoolRef
import org.cef.network.CefRequest

internal class SvgaCefRequestHandler(
    private val svgaFile: VirtualFile
) : CefRequestHandlerAdapter() {

    companion object {
        const val SCHEME = "http"
        const val DOMAIN = "svga-preview"
        const val BASE_URL = "$SCHEME://$DOMAIN"
    }

    private val svgaFileHandler = SvgaFileResourceHandler(svgaFile)
    private val themeHandler = ThemeJsonResourceHandler()
    private val fileInfoHandler = FileInfoResourceHandler(svgaFile, svgaFileHandler.detectSvgaVersion())

    override fun getResourceRequestHandler(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?,
        isNavigation: Boolean,
        isDownload: Boolean,
        requestInitiator: String?,
        disableDefaultHandling: BoolRef?
    ): CefResourceRequestHandler? {
        val url = request?.url ?: return null
        if (!url.startsWith(BASE_URL)) return null

        val path = url.removePrefix(BASE_URL).removePrefix("/")
        disableDefaultHandling?.set(true)
        return route(path)
    }

    private fun route(path: String): CefResourceRequestHandler? {
        return when {
            path.isEmpty() || path == "index.html" ->
                ClasspathResourceHandler("htm/player.htm", "text/html")

            path == "player.css" ->
                ClasspathResourceHandler("htm/player.css", "text/css")

            path == "js/svga.min.js" ->
                ClasspathResourceHandler("js/svga.min.js", "application/javascript")

            path == "js/jszip.min.js" ->
                ClasspathResourceHandler("js/jszip.min.js", "application/javascript")

            path == "js/main.js" ->
                ClasspathResourceHandler("js/main.js", "application/javascript")

            path == "img/backgroundImage.svg" ->
                ClasspathResourceHandler("img/backgroundImage.svg", "image/svg+xml")

            path == "theme.json" ->
                themeHandler

            path == "file.svga" ->
                svgaFileHandler

            path == "file-info.json" ->
                fileInfoHandler

            else -> null
        }
    }
}
