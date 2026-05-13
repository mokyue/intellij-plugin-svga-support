## Context

本项目是一个 IntelliJ IDEA 插件，用于预览和播放 SVGA 动画文件。当前使用 Gradle IntelliJ Plugin 1.x (1.12.0) 构建，运行在 Gradle 7.6 + JDK 8 环境上。

JetBrains 已于 2024 年发布 IntelliJ Platform Gradle Plugin 2.x，并将 1.x 版本标记为不再活跃开发。2.x 带来了全新的 DSL 语法、更好的依赖管理和性能优化，但要求 Gradle 8.13+ 和 JDK 17+。

**当前状态**:
- Gradle: 7.6
- JDK: 1.8
- Plugin: org.jetbrains.intellij 1.12.0
- IDE 兼容范围: 202.* ~ 300.*

**约束条件**:
- 迁移后需保持插件功能完全不变
- 需要更新 IDE 兼容性范围（2.x 最低支持 2022.3）
- 开发环境需升级到 JDK 17+

## Goals / Non-Goals

**Goals:**
- 将 Gradle IntelliJ Plugin 从 1.x 迁移到 2.x
- 升级 Gradle 和 JDK 到官方要求的最低版本
- 保持现有插件功能完整性
- 更新 IDE 兼容性范围以支持最新 IDE 版本
- 优化源码中发现的潜在问题（可选）

**Non-Goals:**
- 添加新功能到插件
- 重构整体代码架构
- 支持多模块项目结构
- 迁移到 Gradle Version Catalog

## Decisions

### Decision 1: Gradle 版本选择

**选择**: Gradle 8.13

**理由**:
- 这是 IntelliJ Platform Gradle Plugin 2.x 的最低要求版本
- 选择最低要求版本可降低迁移风险，减少潜在的兼容性问题
- 后续可根据需要继续升级到更高版本

**替代方案**:
- Gradle 8.14+：功能更多但可能引入额外的 breaking changes

### Decision 2: JDK 版本选择

**选择**: JDK 17

**理由**:
- 这是 IntelliJ Platform Gradle Plugin 2.x 的最低要求
- JDK 17 是 LTS 版本，稳定性好
- 与 Gradle 8.13 兼容性最佳

**替代方案**:
- JDK 21：最新 LTS，但可能引入不必要的复杂性

### Decision 3: IDE 兼容性范围

**选择**: sinceBuild=223, untilBuild=253.*

**理由**:
- 223 (2022.3) 是 IntelliJ Platform Gradle Plugin 2.x 支持的最低版本
- 253.* (2025.3) 提供合理的前向兼容窗口
- 这意味着放弃对 2020.2 ~ 2022.2 的支持

**替代方案**:
- 保持双版本构建：复杂度高，维护成本大

### Decision 4: Kotlin 版本

**选择**: Kotlin 2.0.21

**理由**:
- Kotlin 2.0 是稳定版本，与 JDK 17 兼容
- IntelliJ Platform Plugin Template 推荐使用此版本
- 向后兼容 1.8 语法，无需修改现有源码

**替代方案**:
- Kotlin 1.9.x：兼容但即将过时

### Decision 5: 是否使用 Migration Plugin

**选择**: 初始阶段使用，迁移完成后移除

**理由**:
- `org.jetbrains.intellij.platform.migration` 插件可在 IDE 中提示配置问题
- 帮助发现遗漏的配置项
- 迁移完成后应移除以减少依赖

### Decision 6: 源码优化策略

**选择**: 本次迁移聚焦构建系统，源码优化作为后续独立变更

**理由**:
- 保持变更范围可控
- 便于问题定位（构建问题 vs 代码问题）
- SvgaFileEditorImpl 的内存问题需要更深入的设计

**替代方案**:
- 一次性完成所有变更：风险高，难以回溯问题

## Risks / Trade-offs

### 风险 1: 开发环境升级
**风险**: 团队成员需要升级 JDK 到 17+，可能影响其他项目
**缓解**: 使用 SDKMAN 或 jenv 管理多 JDK 版本；更新 CLAUDE.md 文档说明

### 风险 2: IDE 兼容性断层
**风险**: 放弃 2020.2 ~ 2022.2 支持，可能影响旧版 IDE 用户
**缓解**: 在 Marketplace 说明中注明版本要求；1.x 版本仍可在旧版 IDE 上安装

### 风险 3: 构建配置语法完全不同
**风险**: 新 DSL 学习曲线，可能配置错误
**缓解**: 参考官方 IntelliJ Platform Plugin Template；使用 Migration Plugin 验证

### 风险 4: 签名和发布配置变更
**风险**: signPlugin 和 publishPlugin 配置方式不同，可能导致发布失败
**缓解**: 迁移后在测试环境验证签名流程；保留旧配置作为参考

### 风险 5: 测试不充分
**风险**: 迁移后可能有运行时问题未被发现
**缓解**: 使用 `runIde` 手动测试所有功能；确保 SVGA 1.0 和 2.0 格式都能正常播放

## Migration Plan

### Phase 1: 环境准备
1. 备份当前 `build.gradle.kts`
2. 升级 Gradle Wrapper 到 8.13
3. 确保开发环境有 JDK 17

### Phase 2: 构建配置迁移
1. 更新 Plugin ID 和版本
2. 添加 `intellijPlatform {}` repositories 配置
3. 迁移 `intellij {}` 扩展到 `dependencies { intellijPlatform {} }`
4. 迁移 `patchPluginXml` 到 `intellijPlatform.pluginConfiguration`
5. 迁移 `signPlugin` 和 `publishPlugin` 配置
6. 更新 `gradle.properties` JVM 参数

### Phase 3: 验证
1. 执行 `./gradlew clean build`
2. 执行 `./gradlew runIde` 测试插件功能
3. 测试 SVGA 文件预览（1.0 和 2.0 格式）
4. 执行 `./gradlew signPlugin` 验证签名配置
5. 执行 `./gradlew verifyPlugin` 验证插件兼容性

### Phase 4: 文档更新
1. 更新 `CLAUDE.md` 中的版本兼容性说明
2. 更新 README（如有）

### Rollback Strategy
- 保留旧的 `build.gradle.kts.bak` 备份
- 如迁移失败，还原 Gradle Wrapper 版本和构建文件
- Git 分支管理确保可回退

## Open Questions (Resolved)

1. **Kotlin 语法兼容性**: 现有代码使用 `toUpperCase()` 等已废弃 API，是否需要在本次迁移中修复？
   - ✅ **决定**：需要在本次迁移中修复所有废弃 API

2. **CI/CD 影响**: 如有 GitHub Actions 或其他 CI，是否需要同步更新？
   - ✅ **决定**：需要同步更新 CI/CD 配置

3. **内存优化是否阻塞发布**: SvgaFileEditorImpl 的内存问题是否紧急？
   - ✅ **决定**：需要在本次迁移中解决内存问题

## Additional Requirements

1. **代码注释语言**：所有代码注释使用英文（保持项目国际化）
2. **文档同步**：所有变更需同步更新以下文件：
   - `CLAUDE.md` - 项目指导文档
   - `openspec/project.md` - 项目规格文档
