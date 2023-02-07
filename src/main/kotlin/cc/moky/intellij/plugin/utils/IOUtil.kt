package cc.moky.intellij.plugin.utils

/*******************************************************************************
 * Created on: 2023/2/7 16:48
 * Author: Moky
 * Mail: mokyue@163.com
 *******************************************************************************/

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object IOUtil {

    @JvmStatic
    fun getResourceAsStream(fileName: String): InputStream? {
        return IOUtil::class.java.classLoader.getResourceAsStream(fileName)
    }

    @JvmStatic
    fun getFileContent(fileName: String): String? {
        getResourceAsStream(fileName)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use { reader ->
                val buffer = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    line?.run { buffer.append(this) }
                    buffer.append('\n')
                }
                return buffer.toString()
            }
        }
        return null
    }
}
