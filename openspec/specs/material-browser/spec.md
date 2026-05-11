## ADDED Requirements

### Requirement: Tab 切换交互

系统 SHALL 在 SVGA 预览器顶栏左侧提供"播放"和"素材"两个 Tab 按钮，允许用户在播放视图和素材检视视图之间切换。

- Tab 栏 SHALL 集成到现有 30px 顶栏中，不增加额外垂直空间
- 当前激活的 Tab SHALL 具有视觉区分标识（`.tab-active` class）
- 默认激活"播放"Tab
- 切换 Tab 时 SHALL 通过 CSS `display` 属性控制 `#playerPanel` 和 `#materialPanel` 的可见性
- 切换到"播放"Tab 时 SHALL 显示背景色切换按钮和文件基本信息
- 切换到"素材"Tab 时 SHALL 隐藏背景色切换按钮，显示素材概要信息（内存占用、图片数量）
- 切换 Tab 时 SHALL 同步更新 `#infoDiv` 的内容
- 切换到素材 Tab 时 SHALL NOT 暂停或停止后台播放动画

#### Scenario: 默认加载显示播放 Tab

- **WHEN** SVGA 文件预览器加载完成
- **THEN** "播放"Tab 处于激活状态，播放视图可见，素材视图隐藏

#### Scenario: 从播放切换到素材 Tab

- **WHEN** 用户点击"素材"Tab 按钮
- **THEN** "素材"Tab 获得激活样式，素材视图变为可见，播放视图隐藏，顶栏信息更新为素材概要

#### Scenario: 从素材切换回播放 Tab

- **WHEN** 用户点击"播放"Tab 按钮
- **THEN** "播放"Tab 获得激活样式，播放视图变为可见，素材视图隐藏，顶栏信息恢复为文件基本信息，背景色切换按钮重新显示

#### Scenario: Tab 切换不影响动画播放状态

- **WHEN** 用户从播放 Tab 切换到素材 Tab，然后再切回播放 Tab
- **THEN** SVGA 动画 SHALL 在播放视图中继续播放，无需重新加载

### Requirement: 图片资源列表

系统 SHALL 在素材视图的右侧面板中展示 SVGA 文件内部所有图片资源的列表。

- 列表 SHALL 显示每个图片资源的 key 名称和尺寸信息（宽x高）
- 列表 SHALL 可垂直滚动
- 列表顶部 SHALL 显示总内存占用估算值
- 内存占用估算 SHALL 为所有图片的 `width * height * 4` 字节累加
- 列表项 SHALL 支持悬停高亮效果（使用 `--hover-bg` CSS 变量）
- 首个图片资源 SHALL 默认被选中（高亮显示）
- 面板宽度 SHALL 占素材视图的 40%

#### Scenario: 展示图片资源列表

- **WHEN** 用户切换到素材 Tab
- **THEN** 右侧面板显示所有图片资源项，每项包含 key 和尺寸（如 "10 --- 750x673"），顶部显示内存占用估算

#### Scenario: 滚动长列表

- **WHEN** SVGA 文件包含的图片资源数量超过面板可见区域
- **THEN** 右侧面板 SHALL 可垂直滚动浏览所有资源项

#### Scenario: 列表项悬停反馈

- **WHEN** 用户将鼠标悬停在图片资源列表项上
- **THEN** 该列表项 SHALL 显示悬停背景色

### Requirement: 单图预览

系统 SHALL 在素材视图的左侧面板上方提供图片预览区域，展示当前选中的图片资源。

- 预览区域 SHALL 使用 base64 data URI 渲染图片（`background-image: url(data:image/png;base64,...)`）
- 预览区域 SHALL 使用透明棋盘格背景（复用 `backgroundImage.svg`），与播放画布一致
- 图片 SHALL 居中显示，使用 `background-size: contain` 保持比例
- 点击图片资源列表中的某项 SHALL 更新预览区域显示对应的图片
- 选中的列表项 SHALL 具有视觉区分（`.is-active` class）
- 同时仅有一个列表项处于选中状态
- 预览区域 SHALL 限制最大显示尺寸，避免高分辨率图片导致布局溢出

#### Scenario: 默认显示首张图片

- **WHEN** 用户切换到素材 Tab
- **THEN** 图片预览区域 SHALL 显示第一个图片资源的内容，对应列表项处于选中状态

#### Scenario: 点击列表项切换预览

- **WHEN** 用户点击图片资源列表中的某一项
- **THEN** 预览区域 SHALL 更新为该图片资源的内容，该列表项变为选中状态，之前选中的列表项取消选中

#### Scenario: 高分辨率图片不溢出

- **WHEN** 选中的图片资源尺寸超过预览区域大小
- **THEN** 图片 SHALL 按比例缩放以适应预览区域，不造成布局溢出

### Requirement: JSON 元数据展示

系统 SHALL 在素材视图左侧面板的图片预览区域下方展示 SVGA 文件的结构化元数据。

- 元数据 SHALL 包含 `version`、`FPS`、`frames`、`videoSize`（含 `width` 和 `height`）字段
- 元数据 SHALL 以格式化的 JSON 格式展示
- JSON 代码 SHALL 使用 highlight.js 进行语法高亮
- JSON 代码块 SHALL 可垂直滚动

#### Scenario: 展示 JSON 元数据

- **WHEN** 用户切换到素材 Tab
- **THEN** 左侧面板下方显示格式化的 JSON 元数据，包含 version、FPS、frames、videoSize 字段，且具有 highlight.js 语法高亮

#### Scenario: 长 JSON 可滚动

- **WHEN** JSON 元数据内容超过面板可见区域高度
- **THEN** JSON 代码块 SHALL 可垂直滚动查看全部内容

### Requirement: highlight.js 语法高亮主题适配

系统 SHALL 根据 IDE 当前主题动态切换 highlight.js 的语法高亮主题 CSS。

- 深色主题 SHALL 使用 highlight.js 的 Darcula 主题
- 浅色主题 SHALL 使用 highlight.js 的 GitHub 主题
- 主题切换 SHALL 通过 `onThemeUpdate` 回调触发
- 主题切换 SHALL NOT 产生可见闪烁

#### Scenario: 深色主题下语法高亮

- **WHEN** IDE 处于深色主题
- **THEN** JSON 元数据使用 Darcula 主题的语法高亮配色

#### Scenario: 浅色主题下语法高亮

- **WHEN** IDE 处于浅色主题
- **THEN** JSON 元数据使用 GitHub 主题的语法高亮配色

#### Scenario: 运行时切换 IDE 主题

- **WHEN** 用户在 IDE 运行时从深色主题切换到浅色主题（或反之）
- **THEN** highlight.js 主题 SHALL 相应切换，且无可见闪烁

### Requirement: IDE 主题兼容性

系统 SHALL 确保所有新增 UI 元素兼容 IDE 深色/浅色主题切换。

- Tab 按钮、素材面板、图片列表、JSON 代码块 SHALL 全部使用 CSS 自定义属性控制颜色
- 系统 SHALL 新增 `--tab-active-bg`（Tab 激活态背景色）和 `--hover-bg`（列表项悬停背景色）两个 CSS 自定义属性
- `ThemeJsonBuilder` SHALL 推送这两个新属性的颜色值
- 所有新增样式 SHALL NOT 包含硬编码颜色值

#### Scenario: 深色主题下 UI 元素可读

- **WHEN** IDE 处于深色主题
- **THEN** Tab 按钮、素材面板、图片列表、JSON 代码块的文字和背景 SHALL 保持可读性，与 IDE 原生 UI 视觉一致

#### Scenario: 浅色主题下 UI 元素可读

- **WHEN** IDE 处于浅色主题
- **THEN** Tab 按钮、素材面板、图片列表、JSON 代码块的文字和背景 SHALL 保持可读性，与 IDE 原生 UI 视觉一致

#### Scenario: 运行时主题切换

- **WHEN** 用户在 IDE 运行时切换主题
- **THEN** 所有新增 UI 元素 SHALL 立即反映新主题的颜色，与播放器已有元素同步变化

### Requirement: 素材视图初始化

系统 SHALL 在 SVGA 文件加载完成后自动初始化素材视图的数据。

- 素材视图数据 SHALL 在 SVGA 文件解析完成后（`processSvgaInfo` 调用时）同步初始化
- 图片资源列表 SHALL 动态生成，包含所有 `videoItem.images` 中的条目
- 内存占用估算 SHALL 在初始化时计算完成
- JSON 元数据 SHALL 在初始化时渲染并应用 highlight.js 高亮
- 首次切换到素材 Tab 时 SHALL NOT 出现数据为空或延迟加载

#### Scenario: SVGA 加载后素材数据就绪

- **WHEN** SVGA 文件解析完成，`processSvgaInfo` 被调用
- **THEN** 图片资源列表、内存占用估算、JSON 元数据 SHALL 全部初始化完成，用户切换到素材 Tab 时可立即查看

#### Scenario: 无图片资源的 SVGA 文件

- **WHEN** SVGA 文件不包含任何图片资源（`videoItem.images` 为空）
- **THEN** 素材视图 SHALL 显示空状态提示，内存占用显示为 0
