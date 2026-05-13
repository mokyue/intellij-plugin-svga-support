package cc.moky.intellij.plugin.svga

import com.intellij.openapi.vfs.VirtualFile
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefResourceHandler
import org.cef.handler.CefResourceRequestHandlerAdapter
import org.cef.network.CefRequest
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import kotlin.math.roundToLong

internal class FileInfoResourceHandler(
    private val svgaFile: VirtualFile,
    private val svgaVersion: String
) : CefResourceRequestHandlerAdapter() {

    override fun getResourceHandler(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?
    ): CefResourceHandler {
        val json = buildFileInfoJson()
        val bytes = json.toByteArray(StandardCharsets.UTF_8)
        val stream = ByteArrayInputStream(bytes)
        return StreamCefResourceHandler(stream, "application/json", bytes.size)
    }

    private fun buildFileInfoJson(): String {
        val length = svgaFile.length
        val fileSizeText = formatFileSize(length)
        return """{"fileSize":"$fileSizeText","fileSizeB":$length,"svgaVersion":"$svgaVersion"}"""
    }

    private fun formatFileSize(length: Long): String {
        return when {
            length < 1024 -> "${length}B"
            length < 1048576 -> "${(length * 1.0 / 1024 * 10).roundToLong() / 10.0}K"
            else -> "${(length * 1.0 / 1048576 * 100).roundToLong() / 100.0}M"
        }
    }
}
