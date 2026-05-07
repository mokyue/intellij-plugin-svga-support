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

    private val staticHandlers = mapOf(
        "" to ClasspathResourceHandler("htm/player.htm", "text/html"),
        "index.html" to ClasspathResourceHandler("htm/player.htm", "text/html"),
        "player.css" to ClasspathResourceHandler("htm/player.css", "text/css"),
        "js/svga.min.js" to ClasspathResourceHandler("js/svga.min.js", "application/javascript"),
        "js/jszip.min.js" to ClasspathResourceHandler("js/jszip.min.js", "application/javascript"),
        "js/main.js" to ClasspathResourceHandler("js/main.js", "application/javascript"),
        "img/backgroundImage.svg" to ClasspathResourceHandler("img/backgroundImage.svg", "image/svg+xml"),
    )

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

        val path = extractPath(url)
        disableDefaultHandling?.set(true)
        return route(path)
    }

    private fun extractPath(url: String): String {
        var path = url.removePrefix(BASE_URL).removePrefix("/")
        val queryIndex = path.indexOf('?')
        if (queryIndex >= 0) path = path.substring(0, queryIndex)
        val hashIndex = path.indexOf('#')
        if (hashIndex >= 0) path = path.substring(0, hashIndex)
        return path
    }

    private fun route(path: String): CefResourceRequestHandler? {
        return when (path) {
            in staticHandlers -> staticHandlers[path]
            "theme.json" -> themeHandler
            "file.svga" -> svgaFileHandler
            "file-info.json" -> fileInfoHandler
            else -> null
        }
    }
}
