## Why

当用户使用非 JetBrains Runtime (JBR) 的 JDK 运行 IDE 时，JCEF 不可用，导致 SVGA 预览功能无法工作。目前代码中有 TODO 提示需要实现引导用户切换到 JBR 的功能，但尚未完成。用户在打开 SVGA 文件时无法得到任何提示，体验不佳。

## What Changes

- 在 `SvgaFileEditorProvider.accept()` 中检测 JCEF 不可用时，弹出对话框提示用户
- 对话框使用英文提示：说明当前环境不支持 JCEF
- 对话框包含两个按钮：
  - **"Change JBR"** - 点击后直接打开 "Choose Boot Java Runtime for the IDE" 弹窗
  - **"Cancel"** - 关闭对话框
- 使用 `ActionManager` 调用 `ChooseBootJavaRuntimeAction` 实现一键切换
- 当 JCEF 不可用时，返回 `false` 阻止创建无效的编辑器

## Capabilities

### New Capabilities

- `jcef-availability-dialog`: 当 JCEF 不可用时，显示英文对话框提示用户，提供 "Change JBR" 按钮直接打开 JBR 选择弹窗，以及 "Cancel" 按钮关闭对话框

### Modified Capabilities

<!-- 无需修改现有 specs -->

## Impact

- **代码影响**:
  - `SvgaFileEditorProvider.kt` - 修改 `accept()` 方法，添加对话框和 Action 调用逻辑

- **依赖影响**:
  - 使用 IntelliJ Platform 的 `Messages` API 显示对话框
  - 使用 `ActionManager` API 调用 `ChooseBootJavaRuntimeAction`

- **用户体验**:
  - 用户在 JCEF 不可用时会看到清晰的英文对话框
  - 一键 "Change JBR" 直接打开 JBR 选择弹窗，无需手动查找
  - 提供 "Cancel" 按钮允许用户暂时跳过

- **兼容性**:
  - `Messages` API 在所有目标 IDE 版本 (223.*~253.*) 中稳定可用
  - `ChooseBootJavaRuntimeAction` 是 IDE 内置 Action，无需额外依赖
