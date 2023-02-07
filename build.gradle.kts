group = "cc.moky.intellij.plugin"
version = "1.0.9"
val customSinceBuild = "202."
val customUntilBuild = "300.*"
val customChangeNotes = """
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
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    id("org.jetbrains.intellij") version "1.12.0"
}

repositories {
    mavenCentral()
    google()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
//            https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2020.2")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
    patchPluginXml {
        sinceBuild.set(customSinceBuild)
        untilBuild.set(customUntilBuild)
        changeNotes.set(customChangeNotes)
    }
    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }
    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
