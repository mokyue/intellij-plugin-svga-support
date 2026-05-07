package cc.moky.intellij.plugin.svga

import com.intellij.openapi.vfs.VirtualFile
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefResourceHandler
import org.cef.handler.CefResourceRequestHandlerAdapter
import org.cef.network.CefRequest

internal class SvgaFileResourceHandler(
    private val svgaFile: VirtualFile
) : CefResourceRequestHandlerAdapter() {

    companion object {
        private const val SVGA_V1 = "1.0"
        private const val SVGA_V2 = "2.0"
        private const val SVGA_V1_MAGIC = "504B0304"
    }

    override fun getResourceHandler(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?
    ): CefResourceHandler {
        return StreamCefResourceHandler(svgaFile.inputStream, "application/octet-stream")
    }

    fun detectSvgaVersion(): String {
        val header = readFileHeader()
        return if (SVGA_V1_MAGIC == header) SVGA_V1 else SVGA_V2
    }

    private fun readFileHeader(): String {
        try {
            svgaFile.inputStream.use { stream ->
                val bytes = ByteArray(4)
                stream.read(bytes, 0, bytes.size)
                return bytesToHexString(bytes)
            }
        } catch (_: Exception) {
            return ""
        }
    }

    private fun bytesToHexString(src: ByteArray): String {
        val builder = StringBuilder()
        for (b in src) {
            val hv = Integer.toHexString(b.toInt() and 0xFF).uppercase()
            if (hv.length < 2) {
                builder.append(0)
            }
            builder.append(hv)
        }
        return builder.toString()
    }
}
