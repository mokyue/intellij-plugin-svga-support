## ADDED Requirements

### Requirement: Upgrade Gradle IntelliJ Plugin to 2.x
The build system SHALL migrate from Gradle IntelliJ Plugin 1.x (org.jetbrains.intellij) to IntelliJ Platform Gradle Plugin 2.x (org.jetbrains.intellij.platform).

#### Scenario: Plugin ID update
- **WHEN** build.gradle.kts is updated
- **THEN** the plugin ID MUST be changed from `org.jetbrains.intellij` to `org.jetbrains.intellij.platform`
- **AND** the plugin version MUST be set to `2.11.0` or later

#### Scenario: Extension migration
- **WHEN** the build configuration is migrated
- **THEN** `intellij {}` extension MUST be replaced with `intellijPlatform {}` extension
- **AND** `intellij.version` MUST be replaced with `dependencies { intellijPlatform { intellijIdea("version") } }`

### Requirement: Upgrade Gradle to 8.13
The project SHALL upgrade Gradle wrapper from 7.6 to 8.13 (minimum required by IntelliJ Platform Gradle Plugin 2.x).

#### Scenario: Gradle wrapper update
- **WHEN** gradle/wrapper/gradle-wrapper.properties is updated
- **THEN** distributionUrl MUST point to gradle-8.13-all.zip or later

#### Scenario: Build execution
- **WHEN** `./gradlew build` is executed
- **THEN** the build MUST complete successfully without deprecation warnings related to Gradle version

### Requirement: Upgrade JDK to 17
The project SHALL upgrade from JDK 8 to JDK 17 (minimum required by IntelliJ Platform Gradle Plugin 2.x).

#### Scenario: JVM target update
- **WHEN** build.gradle.kts is updated
- **THEN** sourceCompatibility and targetCompatibility MUST be set to "17"
- **AND** kotlinOptions.jvmTarget MUST be set to "17"

#### Scenario: Gradle JVM configuration
- **WHEN** gradle.properties is updated
- **THEN** org.gradle.jvmargs MUST be compatible with JDK 17

### Requirement: Upgrade Kotlin to 2.0.21
The project SHALL upgrade Kotlin from 1.8.10 to 2.0.21.

#### Scenario: Kotlin plugin version
- **WHEN** build.gradle.kts is updated
- **THEN** org.jetbrains.kotlin.jvm plugin version MUST be set to "2.0.21"

### Requirement: Update IDE compatibility range
The project SHALL update IDE version compatibility from 202.*~300.* to 223.*~253.*.

#### Scenario: sinceBuild update
- **WHEN** plugin.xml or build configuration is updated
- **THEN** sinceBuild MUST be set to "223" (IntelliJ 2022.3)

#### Scenario: untilBuild update
- **WHEN** plugin.xml or build configuration is updated
- **THEN** untilBuild MUST be set to "253.*" (IntelliJ 2025.3.x)

### Requirement: Configure IntelliJ Platform repositories
The project SHALL add IntelliJ Platform specific repositories for dependency resolution.

#### Scenario: Repository configuration
- **WHEN** build.gradle.kts repositories block is updated
- **THEN** it MUST include `intellijPlatform { defaultRepositories() }` within the repositories block

### Requirement: Migrate signPlugin configuration
The signPlugin task SHALL be migrated to the new 2.x configuration format.

#### Scenario: Sign plugin configuration
- **WHEN** signing configuration is migrated
- **THEN** the certificate chain and private key paths MUST be properly configured
- **AND** the PRIVATE_KEY_PASSWORD environment variable handling MUST be preserved

### Requirement: Migrate publishPlugin configuration
The publishPlugin task SHALL be migrated to the new 2.x configuration format.

#### Scenario: Publish plugin configuration
- **WHEN** publish configuration is migrated
- **THEN** the PUBLISH_TOKEN environment variable handling MUST be preserved
- **AND** the plugin MUST be publishable to JetBrains Marketplace

### Requirement: Update documentation
Project documentation SHALL be updated to reflect the new build requirements.

#### Scenario: CLAUDE.md update
- **WHEN** migration is complete
- **THEN** CLAUDE.md MUST be updated with new JDK, Gradle, and IDE version requirements

#### Scenario: project.md update
- **WHEN** migration is complete
- **THEN** openspec/project.md MUST be updated with new build configuration details
