## Why

当前项目使用的 Gradle IntelliJ Plugin 1.x（版本 1.12.0）已被官方标记为**不再活跃开发**，存在以下问题：
1. **依赖过时**：Gradle 7.6 + JDK 8 的组合限制了对新版 IDE 的支持能力
2. **API 废弃风险**：1.x 版本的 `intellij {}` 扩展和任务 API 在未来可能不再兼容
3. **开发体验受限**：缺少 2.x 版本提供的新功能（如更好的依赖管理、多模块支持、性能优化）
4. **IDE 兼容性**：当前配置的 `untilBuild = "300.*"` 已过时，需要适配更新版本的 IntelliJ Platform

## What Changes

### **BREAKING** 构建系统升级
- Gradle IntelliJ Plugin: `1.12.0` → `2.11.0`（IntelliJ Platform Gradle Plugin）
- Plugin ID: `org.jetbrains.intellij` → `org.jetbrains.intellij.platform`
- Gradle: `7.6` → `8.13`（最低要求）
- JDK: `1.8` → `17`（最低要求）
- Kotlin: `1.8.10` → `2.0.21`（推荐）

### 配置语法迁移
- `intellij {}` 扩展 → `intellijPlatform {}` 扩展
- `intellij.version` → `dependencies { intellijPlatform { intellijIdea("版本") } }`
- `patchPluginXml` → `intellijPlatform.pluginConfiguration`
- `signPlugin` / `publishPlugin` → 新的任务配置方式
- 新增 `repositories { intellijPlatform { defaultRepositories() } }`

### IDE 版本兼容性更新
- `sinceBuild`: `202` → `223`（IntelliJ Platform Gradle Plugin 2.x 最低要求 2022.3）
- `untilBuild`: `300.*` → `253.*`（支持到 2025.3.x）

### 源码优化（基于代码审查）
- **SvgaFileEditorImpl**: 每次调用 `getComponent()` 都创建新的 `JBCefBrowser`，存在内存泄漏风险
- **SvgaDataProcessor**: 多次字符串替换操作效率较低
- **IOUtil**: 可优化流处理方式

## Capabilities

### New Capabilities
- `gradle-plugin-migration`: 迁移 Gradle IntelliJ Plugin 1.x 到 2.x 的配置和 API 变更
- `code-optimization`: 修复内存泄漏、废弃 API 和性能问题
- `ci-cd-update`: 更新 CI/CD 配置适配新的构建环境

### Modified Capabilities
（无现有 spec 需要修改）

## Impact

### 受影响的文件
| 文件 | 变更类型 | 说明 |
|-----|---------|------|
| `build.gradle.kts` | **重写** | 全面更新为 2.x 语法 |
| `settings.gradle.kts` | 修改 | 可能需要添加 `pluginManagement` |
| `gradle.properties` | 修改 | 更新 JVM 参数配置 |
| `gradle/wrapper/gradle-wrapper.properties` | 修改 | 升级 Gradle 版本 |
| `src/main/kotlin/**/*.kt` | 修改 | 代码优化和潜在的 API 适配 |
| `src/main/resources/META-INF/plugin.xml` | 可能修改 | 版本兼容性声明 |

### 开发环境要求变更
| 项目 | 当前 | 迁移后 |
|-----|------|-------|
| JDK | 1.8 | 17+ |
| Gradle | 7.6 | 8.13+ |
| IntelliJ IDEA | 2020.2+ | 2022.3+ |
| Kotlin | 1.8.10 | 2.0.21 |

### 风险评估
1. **高风险**：JDK 版本跳跃大（8 → 17），可能需要测试所有功能
2. **中风险**：Gradle 配置语法完全不同，需要熟悉新 DSL
3. **低风险**：源码本身不需要大改，主要是构建配置变更

### 依赖关系
- 本变更不依赖其他变更
- 完成后需要更新 `CLAUDE.md` 中的版本兼容性说明
