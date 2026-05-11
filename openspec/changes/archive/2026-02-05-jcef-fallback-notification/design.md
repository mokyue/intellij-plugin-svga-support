## Context

当前 `SvgaFileEditorProvider.accept()` 方法中已有 `JBCefApp.isSupported()` 检查，但检测到 JCEF 不可用时仅有一个 TODO 注释，未实现任何用户提示。这导致：
1. 用户在非 JBR 环境下打开 SVGA 文件时无任何反馈
2. 可能看到空白编辑器或错误界面
3. 不知道如何解决问题

**当前代码状态**：
```kotlin
override fun accept(project: Project, file: VirtualFile): Boolean {
    if (!JBCefApp.isSupported()) {
        // TODO: 显示"Choose Boot Java Runtime for the IDE"弹窗
    }
    return file.fileType is SvgaFileType
}
```

**约束**：
- 必须兼容 IntelliJ Platform 223.* ~ 253.* (2022.3 ~ 2025.3)
- `accept()` 方法需快速执行，不能阻塞 UI 线程过长时间
- 对话框提示使用英文

## Goals / Non-Goals

**Goals:**
- 当 JCEF 不可用时，向用户显示清晰的英文对话框
- 提供 "Change JBR" 按钮直接打开 JBR 选择对话框
- 提供 "Cancel" 按钮允许用户关闭对话框
- JCEF 不可用时返回 `false`，阻止创建无效编辑器

**Non-Goals:**
- 自动切换 JBR（需要用户手动确认）
- 提供备用渲染方案（如 JavaFX WebView）
- 国际化/多语言支持

## Decisions

### Decision 1: 使用 `Messages.showOkCancelDialog()` 显示对话框

**选择**: 使用 IntelliJ Platform 的 `Messages` API

**理由**:
- API 简单稳定，兼容所有目标 IDE 版本
- 原生支持自定义按钮文本（"Change JBR" / "Cancel"）
- 自动适配 IDE 主题（深色/浅色模式）

**备选方案**:
- ❌ `DialogWrapper` - 过于复杂，简单提示不需要
- ❌ `Notification` API - 通知容易被忽略，对话框更醒目

### Decision 2: 使用 `ActionManager` 调用 `ChooseBootJavaRuntimeAction`

**选择**: 通过 ActionManager 调用 IDE 内置 Action

**理由**:
- `ChooseBootJavaRuntimeAction` 是 IDE 内置功能，稳定可靠
- 无需实现 JBR 选择逻辑，复用现有功能
- Action ID 在不同 IDE 版本中保持一致

**备选方案**:
- ❌ 直接打开 Settings 页面 - 路径可能因版本变化
- ❌ 使用 BrowserUtil 打开文档 - 用户体验差

### Decision 3: 防止对话框重复弹出

**选择**: 使用 `AtomicBoolean` 标志位控制对话框只显示一次

**理由**:
- `accept()` 可能被多次调用（如刷新文件列表时）
- 避免用户看到多个重复对话框
- 原子操作保证线程安全

### Decision 4: 在 EDT 线程上显示对话框

**选择**: 使用 `ApplicationManager.getApplication().invokeLater()` 确保在 EDT 执行

**理由**:
- `accept()` 可能在后台线程调用
- Swing 对话框必须在 EDT (Event Dispatch Thread) 上显示
- `invokeLater()` 不会阻塞当前线程

## Risks / Trade-offs

| Risk | Mitigation |
|------|------------|
| `ChooseBootJavaRuntimeAction` 在某些 IDE 版本不存在 | 在调用前检查 `action != null`，不存在时静默跳过 |
| 对话框可能在 IDE 启动时弹出，影响启动体验 | 只在用户主动打开 SVGA 文件时触发，不影响启动 |
| 用户可能反复看到对话框（每次打开文件） | 使用静态标志位，整个 IDE 会话只提示一次 |
| `accept()` 返回 `false` 后用户无法打开 SVGA 文件 | 对话框已提供解决方案，用户切换 JBR 后即可正常使用 |

## Open Questions

- [已解决] 对话框文案 → 使用英文，简洁明了
- [已解决] 是否需要记住用户选择 → 仅在当前会话记住，不持久化
