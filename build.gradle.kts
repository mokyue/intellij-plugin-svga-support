import org.jetbrains.intellij.platform.gradle.TestFrameworkType

group = "cc.moky.intellij.plugin"
// Use VERSION_TAG from environment (CI), or fallback to default version (local dev)
version = providers.environmentVariable("VERSION_TAG").orElse("0.0.1-beta1").get()

val customChangeNotes = """
<strong>Changes in version 1.1.2:</strong>
<ul>
<li>Fix deprecated API usage.</li>
</ul>
<strong>Changes in version 1.1.1:</strong>
<ul>
<li>Add JCEF support detection and user prompts to guide users to switch to JBR, which supports JCEF, when JCEF is unavailable.</li>
<li>Improve IDE compatibility settings and remove the upper limit to support future versions.</li>
</ul>
<strong>Changes in version 1.1.0:</strong>
<ul>
<li>Migrate to IntelliJ Platform Gradle Plugin 2.x</li>
<li>Upgrade to Gradle 8.13 and JDK 17</li>
<li>Update IDE compatibility range to 2022.3 - 2025.3</li>
<li>Fix JBCefBrowser memory leak</li>
<li>Fix deprecated Kotlin API usage</li>
</ul>
<strong>Changes in version 1.0.9:</strong>
<ul>
<li>Add Mac platform support for plugin.</li>
<li>Update SVGAPlayer version.</li>
</ul>
<strong>Changes in version 1.0.8:</strong>
<ul>
<li>Add SVGA info display (FPS, frames, video size, mem usage, file size, etc).</li>
<li>Add preview background switcher.</li>
<li>Update SVGA version.</li>
<li>Update SWT version to 4.924.</li>
</ul>
<strong>Changes in version 1.0.7:</strong>
<ul>
<li>Fix SVGA file association issue in low idea version.</li>
</ul>
<strong>Changes in version 1.0.6:</strong>
<ul>
<li>Support IDEA 201.*</li>
<li>Add border to canvas background.</li>
</ul>
<strong>Changes in version 1.0.5:</strong>
<ul>
<li>Add grid background to SVGA canvas.</li>
</ul>
<strong>Changes in version 1.0.4:</strong>
<ul>
<li>Fix the tab switch empty page issue in 193.*</li>
<li>Fix the crash issue in 193.*</li>
</ul>
<strong>Changes in version 1.0.3:</strong>
<ul>
<li>Support IDEA 193.*</li>
<li>Update SWT version</li>
<li>Update DJNativeSwing version</li>
</ul>
<strong>Changes in version 1.0.2:</strong>
<ul>
<li>Add SVGA 1.0 support</li>
</ul>
<strong>Changes in version 1.0.1:</strong>
<ul>
<li>Add plugin recommendation</li>
<li>Update scrollbar style</li>
</ul>
<strong>Changes in version 1.0.0:</strong>
<ul>
<li>Created SVGA support plugin for IntelliJ IDEA</li>
</ul>
""".trimIndent()

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.2.0"
    id("org.jetbrains.intellij.platform") version "2.11.0"
}

repositories {
    mavenCentral()
    google()

    intellijPlatform {
        defaultRepositories()
    }
}

// Set to true to use a custom IDE for development/testing
val customIde = false

dependencies {
    intellijPlatform {
        // Use custom ide for development/testing (comment out for CI builds)
        if (customIde) {
            local("/Users/moky/Applications/Android Studio Koala Feature Drop 2024.1.2 Patch 1.app/Contents")
        } else {
            // Use recommended IDE for CI builds
            intellijIdeaCommunity("2022.3")
        }

        pluginVerifier()
        zipSigner()
        testFramework(TestFrameworkType.Platform)
    }
}

// Set the JVM compatibility versions
kotlin {
    jvmToolchain(17)
}

// Configure IntelliJ Platform Gradle Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild.set("223")
            // No upper limit - compatible with all future versions
            untilBuild.set(provider { null })
        }
        changeNotes.set(customChangeNotes)
    }

    pluginVerification {
        ides {
            recommended()
        }
        // Mute the TemplateWordInPluginId warning because this plugin was published
        // to JetBrains Marketplace before this rule was introduced (2024-03-26).
        // Changing the plugin ID would break updates for existing users.
        freeArgs.addAll("-mute", "TemplateWordInPluginId")
    }

    signing {
        certificateChainFile.set(file("chain.crt"))
        privateKeyFile.set(file("private.pem"))
        password.set(providers.environmentVariable("PRIVATE_KEY_PASSWORD"))
    }

    publishing {
        token.set(providers.environmentVariable("PUBLISH_TOKEN"))
    }

    buildSearchableOptions.set(false)
}

tasks {
    runIde {
        autoReload.set(false) // Disable to avoid hot-reload-agent compatibility issues
        maxHeapSize = "4096m"
        // Exclude the problematic hot-reload-agent from JVM args
        jvmArgumentProviders.removeIf {
            it.toString().contains("hot-reload") || it.toString().contains("HotReload")
        }
    }
}
