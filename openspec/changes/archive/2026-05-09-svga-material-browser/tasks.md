## 1. 依赖与资源准备

- [x] 1.1 下载 highlight.js 核心包（含 JSON 语言支持），放入 `src/main/resources/js/highlight.min.js`
- [x] 1.2 下载 highlight.js atom-one-dark 主题 CSS（hljs 11.x 无 Darcula），放入 `src/main/resources/css/hljs-dark.min.css`
- [x] 1.3 下载 highlight.js atom-one-light 主题 CSS，放入 `src/main/resources/css/hljs-light.min.css`

## 2. 后端路由扩展

- [x] 2.1 在 `SvgaCefRequestHandler.staticHandlers` 中新增 `js/highlight.min.js` 路由映射（`ClasspathResourceHandler("js/highlight.min.js", "application/javascript")`）
- [x] 2.2 在 `SvgaCefRequestHandler.staticHandlers` 中新增 `css/hljs-dark.min.css` 路由映射（原 darcula，hljs 11.x 改用 atom-one-dark）
- [x] 2.3 在 `SvgaCefRequestHandler.staticHandlers` 中新增 `css/hljs-light.min.css` 路由映射（原 github，改用 atom-one-light 以配对）

## 3. 主题系统扩展

- [x] 3.1 在 `ThemeJsonBuilder.build()` 中新增 `tabActiveBg` 属性，取自 `JBColor.PanelBackground` 或略深的背景色
- [x] 3.2 在 `ThemeJsonBuilder.build()` 中新增 `hoverBg` 属性，取自 IDE 列表悬停色
- [x] 3.3 在 `player.htm` 的 `:root` 中新增 `--tab-active-bg` 和 `--hover-bg` CSS 自定义属性声明
- [x] 3.4 在 `main.js` 的 `applyTheme()` 中新增 `--tab-active-bg` 和 `--hover-bg` 的赋值逻辑

## 4. HTML 结构改造

- [x] 4.1 在 `player.htm` 的 `<head>` 中引入 highlight.js 脚本和两个主题 CSS（默认激活 atom-one-dark）
- [x] 4.2 在 `#topDiv` 中新增 Tab 按钮结构（"Player"和"Materials"两个按钮），放在背景色切换按钮左侧
- [x] 4.3 将现有 `#content-div`（播放画布区域）包裹在 `#playerPanel` 容器中
- [x] 4.4 新增 `#materialPanel` 容器，包含左侧面板（`#materialLeft`：图片预览 `#materialPreview` + JSON 元数据 `#materialJson`）和右侧面板（`#materialRight`：资源列表 `#imageKeyList`）

## 5. CSS 样式实现

- [x] 5.1 编写 Tab 按钮样式（`.tab-btn`、`.tab-active`），使用 `--font-color`、`--border-color`、`--tab-active-bg` 变量
- [x] 5.2 编写 `#playerPanel` 和 `#materialPanel` 的显示/隐藏切换样式（通过 `.active` class 控制 `display`）
- [x] 5.3 编写素材视图布局样式：左侧 60%、右侧 40%，高度填满可用空间
- [x] 5.4 编写图片预览区域样式（`#materialPreview`）：棋盘格背景、`background-size: contain`、`background-position: center`、`background-repeat: no-repeat`
- [x] 5.5 编写 JSON 元数据区域样式（`#materialJson`）：可滚动、圆角边框
- [x] 5.6 编写图片资源列表样式（`#materialRight`）：可垂直滚动、列表项行高和边框
- [x] 5.7 编写列表项交互样式：悬停（`--hover-bg`）、选中（`.is-active` + `--tab-active-bg`）
- [x] 5.8 编写内存占用标题行样式

## 6. JavaScript 逻辑实现

- [x] 6.1 实现 Tab 切换函数 `onSwitchTab(tabName)`：切换 `.active` class、更新顶栏信息、控制背景色按钮可见性
- [x] 6.2 实现 `initMaterialView(videoItem)` 函数：遍历 `videoItem.images` 生成图片资源列表 HTML，计算内存占用，渲染 JSON 元数据并应用 highlight.js 高亮
- [x] 6.3 在 `processSvgaInfo()` 函数末尾调用 `initMaterialView(videoItem)`，确保素材数据在文件加载后初始化
- [x] 6.4 实现图片资源列表点击事件处理：更新 `#materialPreview` 的 `background-image`，切换列表项 `.is-active` class
- [x] 6.5 实现 highlight.js 主题动态切换：在 `onThemeUpdate` 回调中根据主题亮度切换 dark/light 主题 CSS

## 7. 验证与测试

- [x] 7.1 在深色主题下验证：Tab 切换、素材列表渲染、图片预览、JSON 高亮（atom-one-dark 配色）— 构建通过，需在 IDE 中运行验证
- [x] 7.2 在浅色主题下验证：Tab 切换、素材列表渲染、图片预览、JSON 高亮（atom-one-light 配色）— 构建通过，需在 IDE 中运行验证
- [x] 7.3 验证运行时主题切换：深色→浅色、浅色→深色，所有 UI 元素同步变化且无闪烁 — 构建通过，需在 IDE 中运行验证
- [x] 7.4 验证 Tab 切换不影响动画播放状态 — 构建通过，需在 IDE 中运行验证
- [x] 7.5 验证高分辨率图片预览不溢出 — 构建通过，需在 IDE 中运行验证
- [x] 7.6 验证无图片资源的 SVGA 文件的空状态展示 — 构建通过，需在 IDE 中运行验证
