---
name: svga-local-file-loading
artifact: tasks
status: done
created: 2026-05-06
---

# 任务清单：SVGA 本地文件流式加载

## Phase 1: 基础设施搭建

- [x] **T1.1** 更新 `build.gradle.kts`：`sinceBuild` 从 `223` 改为 `231`
- [x] **T1.2** 创建 `SvgaCefRequestHandler.kt`：实现 `CefRequestHandlerAdapter`，拦截 `http://svga-preview` 域请求，路由到对应资源 handler
- [x] **T1.3** 创建 `ClasspathResourceHandler.kt`：从 classpath 读取静态资源，通过 `JBCefStreamResourceHandler` 返回
- [x] **T1.4** 创建 `SvgaFileResourceHandler.kt`：实现 `CefResourceHandler`，通过 `readResponse()` 分块流式返回 SVGA 文件，自动检测 SVGA 版本

## Phase 2: 主题与页面改造

- [x] **T2.1** 创建 `ThemeJsonResourceHandler.kt`：动态生成当前 IDE 主题颜色 JSON
- [x] **T2.2** 改造 `player.htm`：去除所有占位符，改用外部 `<link>` 和 `<script src>` 引用资源，添加 CSS 变量声明
- [x] **T2.3** 改造 `player.css`：用 CSS 变量（`var(--border-color)` 等）替代占位符
- [x] **T2.4** 改造 `main.js`：
  - 新增 `fetchTheme()` 从 `/theme.json` 获取并应用主题
  - `parser.load()` 从 data URI 改为 `/file.svga`
  - 新增 `window.onThemeUpdate` 回调
  - 背景图改为 `/img/backgroundImage.svg`

## Phase 3: Editor 集成

- [x] **T3.1** 改造 `SvgaFileEditorImpl.kt`：
  - 注册 Per-browser `SvgaCefRequestHandler`
  - `loadHTML()` 改为 `loadURL("http://svga-preview/index.html")`
  - 注册 `LafManagerListener` 监听主题变更
  - 主题变更时通过 `executeJavaScript` 调用 JS 端 `onThemeUpdate()`
- [x] **T3.2** 添加 `/file-info.json` 端点：返回文件大小等元数据，替代 JS 端的 `{FILE_SIZE_STUFF}` 占位符

## Phase 4: 清理

- [x] **T4.1** 删除 `SvgaDataProcessor.kt` 中不再需要的方法（`processHtml`, `processCss`, `buildJsContent`, `processJs`, `fileToBase64`, `resourceToBase64` 及所有占位符常量）
- [x] **T4.2** 将 SVGA 版本检测逻辑（`getSvgaVersion`, `getFileHeader`, `bytesToHexString`）迁移到 `SvgaFileResourceHandler`
- [x] **T4.3** 清理 `SvgaDataProcessor.kt`——如果文件为空则删除

## Phase 5: 验证

- [x] **T5.1** 测试 SVGA v1 文件预览
- [x] **T5.2** 测试 SVGA v2 文件预览
- [x] **T5.3** 测试大文件 (10MB+) 内存占用对比
- [x] **T5.4** 测试 IDE 主题切换（Light → Darcula → Light）时页面颜色自动更新
- [x] **T5.5** 测试同时打开多个 SVGA 文件
- [x] **T5.6** 测试关闭 SVGA tab 后浏览器资源释放

## 依赖关系

```
T1.1 ──┐
T1.2 ──┤
T1.3 ──┼──▶ T3.1 ──▶ T5.x
T1.4 ──┤
T2.1 ──┤
T2.2 ──┤
T2.3 ──┤
T2.4 ──┘
                      T3.1 ──▶ T4.1 ──▶ T4.2 ──▶ T4.3
                      T3.1 ──▶ T3.2
```
