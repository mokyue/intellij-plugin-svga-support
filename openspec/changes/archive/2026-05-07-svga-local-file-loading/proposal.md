---
name: svga-local-file-loading
status: proposed
created: 2026-05-06
---

# SVGA 本地文件流式加载

## 问题

当前 SVGA 文件预览采用 Base64 内嵌方案：将 SVGA 文件完整读入内存，编码为 Base64 字符串，拼接到 HTML 中，再通过 `JBCefBrowser.loadHTML()` 一次性传递给 CEF 渲染。

对 5MB 的 SVGA 文件，峰值内存占用约 35-40MB，链路如下：

```
byte[5MB] → Base64 String(6.7MB) → HTML String(7.5MB) → JNI 复制(7.5MB) → V8 解压(5-10MB)
```

每一环都是全量内存拷贝，Base64 膨胀 33%，且 `loadHTML()` 的内部实现（`file:///jbcefbrowser/<random>` 假 URL 映射）又额外产生一份副本。

## 方案

采用 **Per-browser RequestHandler + `http://svga-preview` 域** 方案，将 SVGA 文件和所有静态资源通过 JCEF 的请求拦截机制流式返回，消除 Base64 编码和 HTML 内联。

### 核心变更

1. **注册 Per-browser RequestHandler**：为每个 `SvgaFileEditorImpl` 实例注册独立的 `CefRequestHandler`，拦截 `http://svga-preview` 域的请求
2. **`loadURL()` 替代 `loadHTML()`**：通过 `browser.loadURL("http://svga-preview/index.html")` 加载页面，CEF 原生资源管理
3. **流式返回 SVGA 文件**：`CefResourceHandler.readResponse()` 分块读取文件，无需全量加载到内存
4. **主题 JSON 端点**：`http://svga-preview/theme.json` 返回当前 IDE 主题颜色，JS 端 fetch 后动态应用
5. **IDE 主题切换同步**：监听 `LafManagerListener`，主题变更时通知 JS 端更新颜色
6. **提升最低版本到 2023.1**：使用 `JBCefStreamResourceHandler` 简化流式资源返回

### 目标架构

```
SvgaFileEditorImpl
  ├── SvgaCefRequestHandler (per-browser, 拦截 http://svga-preview)
  │     ├── /index.html      → player.htm
  │     ├── /player.css      → CSS 文件
  │     ├── /js/svga.min.js  → SVGA 解析库
  │     ├── /js/jszip.min.js → JSZip 库
  │     ├── /js/main.js      → 主逻辑
  │     ├── /img/bg.svg      → 背景图
  │     ├── /theme.json      → 主题颜色配置
  │     └── /file.svga       → SVGA 文件 (流式返回)
  ├── browser.loadURL("http://svga-preview/index.html")
  └── LafManagerListener → 主题变更时推送更新
```

### JS 端变更

- `parser.load()` 从 `data:svga/...;base64,...` 改为 `parser.load('http://svga-preview/file.svga')`
- 新增 `fetch('/theme.json')` 获取并应用主题颜色
- 新增 `window.addEventListener('theme-update', ...)` 监听主题变更

## 内存优化预估

| 场景 | 当前方案 | 改造后 | 节省 |
|------|---------|--------|------|
| 5MB SVGA 峰值 | ~35-40MB | ~15-20MB | ~50% |
| 10MB SVGA 峰值 | ~70-80MB | ~25-35MB | ~55% |
| Base64 字符串 | 文件大小 × 1.33 | 0 | 100% |
| HTML 全量字符串 | 7-8MB+ | 0 (分离加载) | 100% |

## 非目标

- 不改变 SVGA.Parser 库本身
- 不改变 SVGA 文件版本检测逻辑
- 不引入本地 HTTP 服务器
- 不支持自定义 `svga://` scheme（避免 `addCustomScheme` 的复杂性）
- 不改变插件的外部功能表现（背景切换、信息展示等）

## 风险

| 风险 | 缓解 |
|------|------|
| SVGA.Parser 不支持 http URL 加载 | Parser 内部使用 XHR，http URL 是标准场景，需实测验证 |
| Per-browser `addRequestHandler` 在 2023.1 行为差异 | JetBrains 官方插件（如 JCefImageViewer）已使用此 API |
| IDE 主题切换时 JS 端刷新闪烁 | 可在 JS 端做 CSS transition 过渡 |
| `sinceBuild` 从 223 提升到 231 影响用户范围 | 2022.3 已发布 3 年，用户基本已升级 |
