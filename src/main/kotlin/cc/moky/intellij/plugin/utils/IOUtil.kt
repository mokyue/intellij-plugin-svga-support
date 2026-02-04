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

/**
 * Utility object for I/O operations.
 * Provides methods for reading resources from the classpath.
 */
object IOUtil {

    /**
     * Gets a resource as an InputStream from the classpath.
     *
     * @param fileName The resource path relative to the classpath root
     * @return InputStream for the resource, or null if not found
     */
    @JvmStatic
    fun getResourceAsStream(fileName: String): InputStream? {
        return IOUtil::class.java.classLoader.getResourceAsStream(fileName)
    }

    /**
     * Reads the entire content of a classpath resource as a String.
     * Uses UTF-8 encoding and properly closes all streams.
     *
     * @param fileName The resource path relative to the classpath root
     * @return The file content as a String, or null if the resource is not found
     */
    @JvmStatic
    fun getFileContent(fileName: String): String? {
        getResourceAsStream(fileName)?.use { inputStream ->
            // Use BufferedReader for efficient line-by-line reading
            BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use { reader ->
                // Use readText() for more efficient reading of entire content
                return reader.readText()
            }
        }
        return null
    }
}
