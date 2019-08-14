package cc.moky.intellij.plugin.util;

/*******************************************************************************
 * Created on: 2019/8/14 9:40
 * Author: Moky
 * Mail: mokyue@163.com
 *******************************************************************************/

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class SvgaDataProcessor {

    private static final String SVGA_V1 = "1.0";
    private static final String SVGA_V2 = "2.0";
    private static final String CSS_SCRIPT_STUFF = "{CSS_STUFF}";
    private static final String JS_SCRIPT_STUFF = "{JS_SCRIPT_STUFF}";
    private static final String SVGA_DATA_STUFF = "{SVGA_DATA_STUFF}";
    private static final String BACKGROUND_COLOR_STUFF = "#{BACKGROUND_COLOR_STUFF}";

    @NotNull
    public static String processSvgaData(VirtualFile file) {
        if (file == null) {
            return "";
        }
        if (!file.exists()) {
            return "";
        }
        String htmlContent = processHtml(file.getPath());
        if (htmlContent == null) {
            return "";
        }
        return htmlContent;
    }

    @Nullable
    private static String processHtml(String path) {
        String htmlContent = IOUtil.getFileContent("htm/player.htm");
        if (htmlContent == null) {
            return null;
        }
        htmlContent = htmlContent.replace(CSS_SCRIPT_STUFF, processCss());
        String jsContent = String.format("%s\n%s\n%s", processJs("js/jszip.min.js"),
                processJs("js/svga.min.js"), processJs("js/main.js"));
        htmlContent = htmlContent.replace(JS_SCRIPT_STUFF, jsContent);
        Color themeBgColor = JBColor.background().brighter();
        htmlContent = htmlContent.replace(BACKGROUND_COLOR_STUFF, String.format("rgb(%d,%d,%d)",
                themeBgColor.getRed(), themeBgColor.getGreen(), themeBgColor.getBlue()));
        htmlContent = htmlContent.replace(SVGA_DATA_STUFF, String.format("data:svga/%s;base64,%s",
                getSvgaVersion(path), fileToBase64(path)));
        return htmlContent;
    }

    @NotNull
    private static String processJs(String path) {
        String jsContent = IOUtil.getFileContent(path);
        if (jsContent != null) {
            return String.format("<script type=\"text/javascript\">%s</script>", jsContent);
        }
        return "";
    }

    @NotNull
    private static String processCss() {
        String jsContent = IOUtil.getFileContent("htm/player.css");
        if (jsContent != null) {
            return String.format("<style>%s</style>", jsContent);
        }
        return "";
    }

    @NotNull
    private static String getSvgaVersion(String path) {
        return "504B0304".equals(getFileHeader(path)) ? SVGA_V1 : SVGA_V2;
    }

    @NotNull
    private static String getFileHeader(String path) {
        FileInputStream is = null;
        String value = "";
        try {
            is = new FileInputStream(path);
            byte[] b = new byte[4];
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    @NotNull
    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return builder.toString();
        }
        String hv;
        for (byte b : src) {
            hv = Integer.toHexString(b & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }

    private static String fileToBase64(String path) {
        String base64 = null;
        InputStream in = null;
        try {
            File file = new File(path);
            in = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            if (in.read(bytes) != -1) {
                base64 = Base64.getEncoder().encodeToString(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return base64;
    }
}
