---
name: svga-local-file-loading
artifact: design
status: draft
created: 2026-05-06
---

# 设计文档：SVGA 本地文件流式加载

## 概览

将 SVGA 预览从 Base64 内嵌 HTML 方案迁移到 JCEF Per-browser RequestHandler 流式加载方案，消除 Base64 编码和全量 HTML 字符串的内存开销。

## 组件设计

### 1. SvgaCefRequestHandler

每个 `SvgaFileEditorImpl` 实例持有一个 `SvgaCefRequestHandler`，负责拦截 `http://svga-preview` 域的所有请求并路由到对应的资源处理逻辑。

```kotlin
class SvgaCefRequestHandler(
    private val svgaFile: VirtualFile,
    private val disposable: Disposable
) : CefRequestHandlerAdapter() {

    companion object {
        const val SCHEME = "http"
        const val DOMAIN = "svga-preview"
        const val BASE_URL = "$SCHEME://$DOMAIN"
    }

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
                ThemeJsonResourceHandler()
            path == "file.svga" ->
                SvgaFileResourceHandler(svgaFile)
            else -> null
        }
    }
}
```

### 2. 资源 Handler 层次

```
CefResourceRequestHandler (接口)
  │
  ├── ClasspathResourceHandler
  │     从 classpath 读取静态资源 (CSS/JS/SVG/HTML)
  │     使用 JBCefStreamResourceHandler 包装
  │
  ├── ThemeJsonResourceHandler
  │     动态生成当前 IDE 主题颜色的 JSON
  │     每次请求都读取最新的 JBColor 值
  │
  └── SvgaFileResourceHandler
        从 VirtualFile 流式读取 SVGA 文件
        通过 CefResourceHandler.readResponse() 分块返回
        自动检测 SVGA 版本并设置 Content-Type
```

### 3. ClasspathResourceHandler

```kotlin
class ClasspathResourceHandler(
    private val resourcePath: String,
    private val mimeType: String
) : CefResourceRequestHandlerAdapter() {

    override fun getResourceHandler(
        browser: CefBrowser?, frame: CefFrame?,
        request: CefRequest?
    ): CefResourceHandler? {
        val stream = javaClass.classLoader.getResourceAsStream(resourcePath) ?: return null
        return JBCefStreamResourceHandler(stream, mimeType, disposable)
    }
}
```

### 4. ThemeJsonResourceHandler

```kotlin
class ThemeJsonResourceHandler : CefResourceRequestHandlerAdapter() {

    override fun getResourceHandler(
        browser: CefBrowser?, frame: CefFrame?,
        request: CefRequest?
    ): CefResourceHandler {
        val theme = currentThemeJson()
        val bytes = theme.toByteArray(StandardCharsets.UTF_8)
        return JBCefStreamResourceHandler(
            ByteArrayInputStream(bytes),
            "application/json",
            disposable
        )
    }

    private fun currentThemeJson(): String {
        val border = JBColor.border()
        val bg = JBColor.background()
        val font = JBColor.foreground()
        val fontFamily = UIUtil.getLabelFont().family
        return """{
            "borderColor": "rgb(${border.red},${border.green},${border.blue})",
            "backgroundColor": "rgb(${bg.red},${bg.green},${bg.blue})",
            "fontColor": "rgb(${font.red},${font.green},${font.blue})",
            "fontFamily": "$fontFamily"
        }"""
    }
}
```

### 5. SvgaFileResourceHandler

```kotlin
class SvgaFileResourceHandler(
    private val svgaFile: VirtualFile
) : CefResourceHandlerAdapter() {

    private var inputStream: InputStream? = null
    private var version: String = ""

    override fun processRequest(request: CefRequest?, callback: CefCallback?): Boolean {
        version = detectSvgaVersion(svagaFile)
        inputStream = svgaFile.inputStream
        callback?.Continue()
        return true
    }

    override fun getResponseHeaders(
        response: CefResponse?, responseLength: IntRef?,
        redirectUrl: StringRef?
    ) {
        response?.apply {
            setStatus(200)
            setMimeType("application/octet-stream")
            setHeaderByName("Content-Type", "application/svga", true)
        }
        responseLength?.set(-1) // 未知长度, 流式传输
    }

    override fun readResponse(
        dataOut: ByteArray?, bytesToRead: Int,
        bytesRead: IntRef?, callback: CefCallback?
    ): Boolean {
        val stream = inputStream ?: return false
        val read = stream.read(dataOut, 0, bytesToRead)
        if (read <= 0) {
            inputStream?.close()
            inputStream = null
            return false
        }
        bytesRead?.set(read)
        return true
    }

    override fun cancel() {
        inputStream?.close()
        inputStream = null
    }
}
```

### 6. SvgaFileEditorImpl 改造

```kotlin
internal class SvgaFileEditorImpl(private val mFile: VirtualFile) : UserDataHolderBase(), FileEditor {

    private var browser: JBCefBrowser? = null
    private var browserComponent: JComponent? = null

    override fun getComponent(): JComponent {
        browserComponent?.let { return it }

        val newBrowser = JBCefBrowser()

        // 注册 Per-browser RequestHandler
        val handler = SvgaCefRequestHandler(mFile, newBrowser)
        newBrowser.jbCefClient.addRequestHandler(handler, newBrowser.cefBrowser)

        // 通过 loadURL 加载页面 (替代 loadHTML)
        newBrowser.loadURL("${SvgaCefRequestHandler.BASE_URL}/index.html")

        browser = newBrowser
        browserComponent = newBrowser.component

        return newBrowser.component
    }

    // ... 其余方法不变
}
```

### 7. 主题切换监听

```kotlin
internal class SvgaFileEditorImpl(private val mFile: VirtualFile) : ... {

    // 监听 IDE 主题变更
    private val lafListener = object : LafManagerListener {
        override fun lookAndFeelChanged(source: LafManager?) {
            notifyThemeChanged()
        }
    }

    private fun notifyThemeChanged() {
        browser?.let { b ->
            b.cefBrowser.executeJavaScript(
                "if (window.onThemeUpdate) window.onThemeUpdate();",
                "${SvgaCefRequestHandler.BASE_URL}/index.html",
                0
            )
        }
    }

    override fun getComponent(): JComponent {
        // ... (注册 handler, loadURL)

        // 注册主题监听
        ApplicationManager.getApplication().messageBus
            .connect(this)
            .subscribe(LafManagerListener.TOPIC, lafListener)

        return newBrowser.component
    }
}
```

## JS 端变更

### main.js

```javascript
// 主题管理
let currentTheme = null;

function applyTheme(theme) {
    currentTheme = theme;
    document.documentElement.style.setProperty('--border-color', theme.borderColor);
    document.documentElement.style.setProperty('--background-color', theme.backgroundColor);
    document.documentElement.style.setProperty('--font-color', theme.fontColor);
    document.documentElement.style.setProperty('--font-family', theme.fontFamily);
}

function fetchTheme() {
    fetch('/theme.json')
        .then(r => r.json())
        .then(theme => applyTheme(theme));
}

// 主题变更回调 (由 Java 端触发)
window.onThemeUpdate = function() {
    fetchTheme();
};

// 页面加载
function onPageLoaded() {
    // 先加载主题, 再初始化播放器
    fetchTheme().then(() => {
        let player = new SVGA.Player('#playerCanvas');
        let parser = new SVGA.Parser('#playerCanvas');
        parser.load('/file.svga', function(videoItem) {
            document.getElementById('playerCanvas').style.width =
                ''.concat(videoItem.videoSize.width, 'px');
            document.getElementById('playerCanvas').style.height =
                ''.concat(videoItem.videoSize.height, 'px');
            player.setVideoItem(videoItem);
            player.startAnimation();
            processSvgaInfo(videoItem);
        });
    });
}
```

### player.htm

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>SVGA Player</title>
    <link rel="stylesheet" href="player.css">
    <style>
        :root {
            --border-color: rgb(60, 60, 60);
            --background-color: rgb(45, 45, 48);
            --font-color: rgb(187, 187, 187);
            --font-family: sans-serif;
        }
    </style>
    <script src="js/jszip.min.js"></script>
    <script src="js/svga.min.js"></script>
    <script src="js/main.js"></script>
</head>
<body onload="onPageLoaded()" oncontextmenu="return false;" onselectstart="return false;">
<div id="topDiv">
    <div class="switch-bg-btn" id="switch-bg-none" onclick="onSwitchBackground(this)"></div>
    <div class="switch-bg-btn" id="switch-bg-black" onclick="onSwitchBackground(this)"></div>
    <div class="switch-bg-btn" id="switch-bg-white" onclick="onSwitchBackground(this)"></div>
    <div class="switch-bg-btn" id="switch-bg-blue" onclick="onSwitchBackground(this)"></div>
    <div class="switch-bg-btn" id="switch-bg-green" onclick="onSwitchBackground(this)"></div>
    <div class="switch-bg-btn" id="switch-bg-yellow" onclick="onSwitchBackground(this)"></div>
    <div class="switch-bg-btn" id="switch-bg-red" onclick="onSwitchBackground(this)"></div>
    <div id="infoDiv"></div>
</div>
<div id="content-div">
    <div id="playerCanvas"></div>
</div>
</body>
</html>
```

### player.css

```css
/* 使用 CSS 变量替代硬编码占位符 */
body {
    background-color: var(--background-color);
    color: var(--font-color);
    font-family: var(--font-family);
}

#playerCanvas {
    border-color: var(--border-color);
    /* ... */
}
```

## 废弃的组件

以下组件/逻辑将被移除或大幅简化：

| 组件 | 处理 |
|------|------|
| `SvgaDataProcessor.processHtml()` | 删除 — 不再需要 HTML 模板替换 |
| `SvgaDataProcessor.processCss()` | 删除 — CSS 通过独立文件加载 |
| `SvgaDataProcessor.buildJsContent()` | 删除 — JS 通过 `<script src>` 加载 |
| `SvgaDataProcessor.processJs()` | 删除 |
| `SvgaDataProcessor.fileToBase64()` | 删除 — SVGA 文件流式传输 |
| `SvgaDataProcessor.resourceToBase64()` | 删除 — 背景图通过独立 URL 加载 |
| `SvgaDataProcessor.processFileSizeText()` | 保留 — 仍需在 JS 端显示，可通过 `/file-info.json` 端点提供 |
| `SvgaDataProcessor.getSvgaVersion()` | 保留 — 迁移到 SvgaFileResourceHandler |
| `SvgaDataProcessor.getFileHeader()` | 保留 — 被 getSvgaVersion 使用 |
| `SvgaDataProcessor.bytesToHexString()` | 保留 — 被 getFileHeader 使用 |
| HTML 中的所有占位符常量 | 删除 |
| `getImageSizeFromBase64Data()` (JS) | 保留 — 不变 |

## 版本兼容

- **sinceBuild**: `223` → `231` (IntelliJ 2023.1+)
- 原因：使用 `JBCefStreamResourceHandler` (2023.1+ 可用)
- 影响：2022.3 用户需升级 IDE 才能使用新版本插件

## 文件变更清单

| 操作 | 文件 |
|------|------|
| 新增 | `SvgaCefRequestHandler.kt` — 请求路由与拦截 |
| 新增 | `ClasspathResourceHandler.kt` — Classpath 资源返回 |
| 新增 | `ThemeJsonResourceHandler.kt` — 主题颜色 JSON 端点 |
| 新增 | `SvgaFileResourceHandler.kt` — SVGA 文件流式返回 |
| 修改 | `SvgaFileEditorImpl.kt` — 注册 handler + loadURL + 主题监听 |
| 修改 | `main.js` — fetch 主题 + URL 加载 SVGA + 主题更新回调 |
| 修改 | `player.htm` — 外部引用 CSS/JS, 去除占位符 |
| 修改 | `player.css` — CSS 变量替代占位符 |
| 修改 | `build.gradle.kts` — sinceBuild 223 → 231 |
| 删除 | `SvgaDataProcessor.kt` — 大部分方法不再需要 |
