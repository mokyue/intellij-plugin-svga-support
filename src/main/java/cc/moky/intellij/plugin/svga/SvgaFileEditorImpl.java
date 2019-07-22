package cc.moky.intellij.plugin.svga;

/*******************************************************************************
 * Created on: 2019/7/16 9:48
 * Author: Moky
 * Mail: mokyue@163.com
 *******************************************************************************/

import cc.moky.intellij.plugin.util.IOUtil;
import chrriis.dj.nativeswing.NSComponentOptions;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;
import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import com.intellij.openapi.fileEditor.impl.text.TextEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

final class SvgaFileEditorImpl extends UserDataHolderBase implements FileEditor {

    private static final String NAME = "SVGA File Editor";
    private static final String CSS_SCRIPT_STUFF = "{CSS_STUFF}";
    private static final String JS_SCRIPT_STUFF = "{JS_SCRIPT_STUFF}";
    private static final String SVGA_DATA_STUFF = "{SVGA_DATA_STUFF}";
    private static final String BACKGROUND_COLOR_STUFF = "#{BACKGROUND_COLOR_STUFF}";
    private final VirtualFile mFile;

    SvgaFileEditorImpl(@NotNull Project project, @NotNull VirtualFile file) {
        mFile = file;
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        NativeInterface.open();
        JWebBrowser browser = new JWebBrowser(NSComponentOptions.destroyOnFinalization());
        browser.setMenuBarVisible(false);
        browser.setBarsVisible(false);
        browser.setLocationBarVisible(false);
        browser.setButtonBarVisible(false);
        browser.setStatusBarVisible(false);
        browser.setDefaultPopupMenuRegistered(false);
        browser.setJavascriptEnabled(true);
        browser.setVisible(false);
        browser.addWebBrowserListener(new WebBrowserAdapter() {
            @Override
            public void loadingProgressChanged(WebBrowserEvent e) {
                if (e == null) {
                    return;
                }
                JWebBrowser browser = e.getWebBrowser();
                if (browser == null) {
                    return;
                }
                if (browser.getLoadingProgress() == 100) {
                    if (!browser.isVisible()) {
                        browser.setVisible(true);
                    }
                } else {
                    if (browser.isVisible()) {
                        browser.setVisible(false);
                    }
                }
            }
        });
        browser.setHTMLContent(processHTMLContent());
        return browser;
    }

    private String processHTMLContent() {
        String htmlContent = IOUtil.getFileContent("htm/player.htm");
        if (htmlContent != null) {
            String cssContent = processCssContent("htm/player.css");
            if (cssContent != null) {
                htmlContent = htmlContent.replace(CSS_SCRIPT_STUFF, cssContent);
            }
            String jsContent = String.format("%s\n%s\n%s", processJsContent("js/jszip.min.js"),
                    processJsContent("js/svga.min.js"), processJsContent("js/main.js"));
            htmlContent = htmlContent.replace(JS_SCRIPT_STUFF, jsContent);
            Color themeBgColor = JBColor.background().brighter();
            htmlContent = htmlContent.replace(BACKGROUND_COLOR_STUFF, String.format("rgb(%d,%d,%d)",
                    themeBgColor.getRed(), themeBgColor.getGreen(), themeBgColor.getBlue()));
            htmlContent = htmlContent.replace(SVGA_DATA_STUFF, String.format("data:svga/2.0;base64,%s", fileToBase64(mFile.getPath())));
        }
        return htmlContent;
    }

    private String processJsContent(String path) {
        String jsContent = IOUtil.getFileContent(path);
        if (jsContent != null) {
            return String.format("<script type=\"text/javascript\">%s</script>", jsContent);
        }
        return null;
    }

    private String processCssContent(String path) {
        String jsContent = IOUtil.getFileContent(path);
        if (jsContent != null) {
            return String.format("<style>%s</style>", jsContent);
        }
        return null;
    }

    private String fileToBase64(String path) {
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

    @Override
    public JComponent getPreferredFocusedComponent() {
        return null;
    }

    @NotNull
    @Override
    public String getName() {
        return NAME;
    }

    @NotNull
    @Override
    public FileEditorState getState(@NotNull FileEditorStateLevel level) {
        return new TextEditorState();
    }

    @Override
    public void setState(@NotNull FileEditorState state) {
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return mFile.isValid();
    }

    @Override
    public void selectNotify() {
    }

    @Override
    public void deselectNotify() {
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    @Override
    public BackgroundEditorHighlighter getBackgroundHighlighter() {
        return null;
    }

    @Override
    public FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    public StructureViewBuilder getStructureViewBuilder() {
        return null;
    }

    @Override
    public void dispose() {
    }
}
