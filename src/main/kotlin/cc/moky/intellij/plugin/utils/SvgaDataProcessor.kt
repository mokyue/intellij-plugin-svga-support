package cc.moky.intellij.plugin.utils

/*******************************************************************************
 * Created on: 2023/2/7 19:07
 * Author: Moky
 * Mail: mokyue@163.com
 *******************************************************************************/

import cc.moky.intellij.plugin.utils.IOUtil.getFileContent
import cc.moky.intellij.plugin.utils.IOUtil.getResourceAsStream
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import com.intellij.util.ui.UIUtil
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.math.roundToLong

object SvgaDataProcessor {

    private const val SVGA_V1 = "1.0"
    private const val SVGA_V2 = "2.0"
    private const val CSS_SCRIPT_STUFF = "{CSS_STUFF}"
    private const val JS_SCRIPT_STUFF = "{JS_SCRIPT_STUFF}"
    private const val SVGA_DATA_STUFF = "{SVGA_DATA_STUFF}"
    private const val FONT_FAMILY_STUFF = "{FONT_FAMILY_STUFF}"
    private const val FONT_COLOR_STUFF = "#{FONT_COLOR_STUFF}"
    private const val BORDER_COLOR_STUFF = "#{BORDER_COLOR_STUFF}"
    private const val BACKGROUND_COLOR_STUFF = "#{BACKGROUND_COLOR_STUFF}"
    private const val BACKGROUND_IMAGE_STUFF = "{BACKGROUND_IMAGE_STUFF}"
    private const val FILE_SIZE_STUFF = "{FILE_SIZE_STUFF}"

    @JvmStatic
    fun processSvgaData(file: VirtualFile): String {
        return if (file.exists()) (processHtml(file.path) ?: "") else ""
    }

    private fun processHtml(path: String): String? {
        var htmlContent = getFileContent("htm/player.htm") ?: return null
        htmlContent = htmlContent.replace(CSS_SCRIPT_STUFF, processCss())
        htmlContent = htmlContent.replace(JS_SCRIPT_STUFF, buildJsContent())
        htmlContent = htmlContent.replace(FILE_SIZE_STUFF, processFileSizeText(path))
        val borderColor = JBColor.border()
        htmlContent = htmlContent.replace(BORDER_COLOR_STUFF,
                "rgb(${borderColor.red},${borderColor.green},${borderColor.blue})")
        val themeBgColor = JBColor.background()
        htmlContent = htmlContent.replace(BACKGROUND_COLOR_STUFF,
                "rgb(${themeBgColor.red},${themeBgColor.green},${themeBgColor.blue})")
        val fontColor = JBColor.foreground()
        htmlContent = htmlContent.replace(FONT_COLOR_STUFF,
                "rgb(${fontColor.red},${fontColor.green},${fontColor.blue})")
        htmlContent = htmlContent.replace(FONT_FAMILY_STUFF, UIUtil.getLabelFont().family)
        htmlContent = htmlContent.replace(BACKGROUND_IMAGE_STUFF,
                "data:image/svg+xml;base64,${resourceToBase64("img/backgroundImage.svg")}")
        htmlContent = htmlContent.replace(SVGA_DATA_STUFF,
                "data:svga/${getSvgaVersion(path)};base64,${fileToBase64(path)}")
        return htmlContent
    }

    private fun buildJsContent(): String {
        return """
            ${processJs("js/svga.min.js")}
            ${processJs("js/jszip.min.js")}
            ${processJs("js/main.js")}
        """.trimIndent()
    }

    private fun processJs(path: String): String {
        val jsContent = getFileContent(path)
        return if (jsContent != null) "<script type=\"text/javascript\">${jsContent}</script>" else ""
    }

    private fun processCss(): String {
        val jsContent = getFileContent("htm/player.css")
        return if (jsContent != null) "<style>${jsContent}</style>" else ""
    }

    private fun getSvgaVersion(path: String): String {
        return if ("504B0304" == getFileHeader(path)) SVGA_V1 else SVGA_V2
    }

    private fun getFileHeader(path: String): String {
        var value = ""
        FileInputStream(path).use { inputStream ->
            val b = ByteArray(4)
            inputStream.read(b, 0, b.size)
            value = bytesToHexString(b)
        }
        return value
    }

    private fun processFileSizeText(filePath: String): String {
        val length = File(filePath).length()
        return if (length < 1024) {
            "${length}B"
        } else if (length < 1048576) {
            "${(length * 1.0 / 1024 * 10).roundToLong() / 10.0}K"
        } else {
            "${(length * 1.0 / 1048576 * 100).roundToLong() / 100.0}M"
        }
    }

    private fun bytesToHexString(src: ByteArray?): String {
        val builder = StringBuilder()
        if (src == null || src.isEmpty()) {
            return builder.toString()
        }
        var hv: String
        for (b in src) {
            hv = Integer.toHexString(b.toInt() and 0xFF).toUpperCase()
            if (hv.length < 2) {
                builder.append(0)
            }
            builder.append(hv)
        }
        return builder.toString()
    }

    private fun fileToBase64(filePath: String): String {
        var base64 = ""
        val file = File(filePath)
        FileInputStream(file).use { inputStream ->
            val bytes = ByteArray(file.length().toInt())
            if (inputStream.read(bytes) != -1) {
                base64 = Base64.getEncoder().encodeToString(bytes)
            }
        }
        return base64
    }

    private fun resourceToBase64(resPath: String): String {
        var base64 = ""
        getResourceAsStream(resPath)?.use { inputStream ->
            val bytes = ByteArray(inputStream.available())
            if (inputStream.read(bytes) != -1) {
                base64 = Base64.getEncoder().encodeToString(bytes)
            }
        }
        return base64
    }
}
