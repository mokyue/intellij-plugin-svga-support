package cc.moky.intellij.plugin.svga

import org.cef.callback.CefCallback
import org.cef.handler.CefResourceHandlerAdapter
import org.cef.misc.IntRef
import org.cef.misc.StringRef
import org.cef.network.CefResponse
import java.io.InputStream

internal class StreamCefResourceHandler(
    private val stream: InputStream,
    private val mimeType: String,
    private val contentLength: Int = -1
) : CefResourceHandlerAdapter() {

    override fun processRequest(request: org.cef.network.CefRequest?, callback: CefCallback?): Boolean {
        callback?.Continue()
        return true
    }

    override fun getResponseHeaders(
        response: CefResponse?,
        responseLength: IntRef?,
        redirectUrl: StringRef?
    ) {
        response?.apply {
            status = 200
            mimeType = this@StreamCefResourceHandler.mimeType
        }
        if (contentLength >= 0) {
            responseLength?.set(contentLength)
        } else {
            responseLength?.set(-1)
        }
    }

    override fun readResponse(
        dataOut: ByteArray?,
        bytesToRead: Int,
        bytesRead: IntRef?,
        callback: CefCallback?
    ): Boolean {
        if (dataOut == null) return false
        val read = stream.read(dataOut, 0, bytesToRead)
        if (read <= 0) {
            closeStream()
            return false
        }
        bytesRead?.set(read)
        return true
    }

    override fun cancel() {
        closeStream()
    }

    private fun closeStream() {
        try {
            stream.close()
        } catch (_: Exception) {
            // ignore
        }
    }
}
