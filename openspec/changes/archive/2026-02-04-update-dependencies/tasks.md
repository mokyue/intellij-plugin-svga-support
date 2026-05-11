## 1. Environment Preparation

- [x] 1.1 Backup current build.gradle.kts to build.gradle.kts.bak
- [x] 1.2 Upgrade Gradle Wrapper to 8.13 (`./gradlew wrapper --gradle-version 8.13`)
- [x] 1.3 Verify JDK 17 is available in development environment
- [x] 1.4 Update gradle.properties JVM args for JDK 17 compatibility

## 2. Build Configuration Migration

- [x] 2.1 Update plugin ID from `org.jetbrains.intellij` to `org.jetbrains.intellij.platform`
- [x] 2.2 Update plugin version to `2.11.0`
- [x] 2.3 Add migration plugin `org.jetbrains.intellij.platform.migration` temporarily
- [x] 2.4 Update Kotlin plugin version from `1.8.10` to `2.0.21`
- [x] 2.5 Add `intellijPlatform { defaultRepositories() }` to repositories block
- [x] 2.6 Replace `intellij { version.set("2020.2") }` with `dependencies { intellijPlatform { intellijIdea("2022.3") } }`
- [x] 2.7 Update JVM target from "1.8" to "17" (sourceCompatibility, targetCompatibility, kotlinOptions.jvmTarget)
- [x] 2.8 Migrate `patchPluginXml` task to `intellijPlatform.pluginConfiguration`
- [x] 2.9 Update sinceBuild to "223" and untilBuild to "253.*"
- [x] 2.10 Migrate `signPlugin` task configuration to 2.x format
- [x] 2.11 Migrate `publishPlugin` task configuration to 2.x format
- [x] 2.12 Migrate `runIde` and `buildSearchableOptions` configurations
- [x] 2.13 Remove migration plugin after successful migration

## 3. Code Optimization - Memory Leak Fix

- [x] 3.1 Add private `browser: JBCefBrowser?` field to SvgaFileEditorImpl
- [x] 3.2 Implement lazy initialization in getComponent() to cache browser instance
- [x] 3.3 Implement proper disposal in dispose() method to release browser resources
- [x] 3.4 Add English comments explaining the caching mechanism

## 4. Code Optimization - Deprecated API Fix

- [x] 4.1 Replace `toUpperCase()` with `uppercase()` in SvgaDataProcessor.bytesToHexString()
- [x] 4.2 Search for other deprecated Kotlin API usages and fix them
- [x] 4.3 Add English comments for any non-trivial changes

## 5. Code Optimization - Performance (Optional)

- [x] 5.1 Review SvgaDataProcessor.processHtml() for string optimization opportunities
- [x] 5.2 Verify IOUtil stream handling uses proper buffer sizes
- [x] 5.3 Ensure file reading operations handle large files efficiently

## 6. CI/CD Configuration

- [x] 6.1 Check if .github/workflows directory exists
- [x] 6.2 Create or update GitHub Actions workflow (build.yml)
- [x] 6.3 Configure workflow to use JDK 17 (actions/setup-java with temurin)
- [x] 6.4 Enable Gradle caching in workflow
- [x] 6.5 Add build, test, and verifyPlugin steps
- [x] 6.6 Configure triggers for push and pull_request to main/master

## 7. Verification

- [x] 7.1 Execute `./gradlew clean build` and verify success
- [ ] 7.2 Execute `./gradlew runIde` and test SVGA preview functionality
- [ ] 7.3 Test SVGA 1.0 format file playback
- [ ] 7.4 Test SVGA 2.0 format file playback
- [ ] 7.5 Verify dark/light theme compatibility
- [x] 7.6 Execute `./gradlew verifyPlugin` and check compatibility
- [ ] 7.7 Execute `./gradlew signPlugin` with test credentials (if available)

## 8. Documentation Update

- [x] 8.1 Update CLAUDE.md version compatibility section (JDK 17, Gradle 8.13, IDE 223~253.*)
- [x] 8.2 Update CLAUDE.md build commands if any changes
- [x] 8.3 Update openspec/project.md with new build configuration details
- [x] 8.4 Translate any Chinese code comments to English
- [x] 8.5 Review and update README.md if exists

## 9. Cleanup and Commit

- [x] 9.1 Remove build.gradle.kts.bak backup file
- [x] 9.2 Run final verification (`./gradlew clean build verifyPlugin`)
- [~] 9.3 Commit changes with descriptive message following conventional commits (用户自行处理)
- [~] 9.4 Push changes and verify CI/CD pipeline passes (用户自行处理)
