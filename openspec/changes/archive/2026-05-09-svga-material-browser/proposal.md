## Why

当前 IntelliJ SVGA 插件仅支持动画播放和基本文件信息展示，无法查看 SVGA 文件内部的图片资源和结构化元数据。开发者在调试或审查 SVGA 文件时，需要切换到外部工具才能检视内部资源构成，效率低下。svga.dev 在线播放器的"浏览素材"功能已验证了这一需求的实用性，现将其核心能力移植到 IDE 内，以提升开发体验。

## What Changes

- 新增内嵌 Tab 切换 UI，在播放页面和素材检视页面之间切换（方案B）
- 新增素材检视页面，展示 SVGA 文件内部图片资源列表（key + 尺寸信息）
- 新增点击图片资源项后的单图预览功能（base64 渲染）
- 新增 JSON 元数据展示区域（version、FPS、frames、videoSize），使用 highlight.js 做语法高亮
- 新增内存占用估算显示
- Tab 切换 UI 和素材检视页面兼容 IDE 深色/浅色主题切换
- UI 设计风格与现有播放器页面保持一致

## Capabilities

### New Capabilities

- `material-browser`: SVGA 文件内部资源检视能力，包括图片资源列表浏览、单图预览、JSON 元数据展示（highlight.js 语法高亮）、内存占用估算，以及播放/素材的 Tab 切换交互

### Modified Capabilities

（无既有 spec 需要修改）

## Impact

- **前端资源**: `player.htm` 新增 Tab UI 结构和素材页面 HTML；`player.css` 新增 Tab 和素材检视样式；`main.js` 新增 Tab 切换逻辑、素材初始化、图片预览交互
- **后端路由**: `SvgaCefRequestHandler` 需新增 highlight.js 相关静态资源的路由映射
- **新增依赖**: highlight.js 库文件（放入 `resources/js/`）
- **已有代码**: `processSvgaInfo()` 和 `getImageSizeFromBase64Data()` 函数将被复用和扩展
- **主题系统**: 新增 UI 元素需通过 CSS 自定义属性接入现有 `onThemeUpdate` 主题推送机制
