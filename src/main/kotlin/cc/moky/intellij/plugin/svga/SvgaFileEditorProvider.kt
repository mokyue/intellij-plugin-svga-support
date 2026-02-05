package cc.moky.intellij.plugin.svga

/*******************************************************************************
 * Created on: 2023/2/7 19:09
 * Author: Moky
 * Mail: mokyue@163.com
 *******************************************************************************/

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.jcef.JBCefApp
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Flag to ensure the JCEF warning dialog is shown only once per IDE session.
 * Using AtomicBoolean for thread safety as accept() may be called from multiple threads.
 */
private val jcefWarningShown = AtomicBoolean(false)

/**
 * Possible Action IDs for the "Choose Boot Java Runtime" dialog.
 * Different IDE versions may use different action IDs.
 */
private val CHOOSE_RUNTIME_ACTION_IDS = arrayOf(
    "ChooseRuntime",                               // Most common ID (Choose Runtime plugin)
    "ChooseBootRuntime",                           // Alternative ID
    "ChooseBootJavaRuntimeAction",                 // Legacy ID
    "com.intellij.ide.actions.ChooseRuntimeAction" // Full qualified name
)

internal class SvgaFileEditorProvider : FileEditorProvider, DumbAware {

    companion object {
        private val LOG = Logger.getInstance(SvgaFileEditorProvider::class.java)

        private const val DIALOG_TITLE = "JCEF Not Available"
        private const val DIALOG_MESSAGE = """SVGA preview requires JCEF which is not available in your current runtime.

Please switch to a JetBrains Runtime (JBR) with JCEF support."""
        private const val BUTTON_CHANGE_JBR = "Change JBR"
        private const val BUTTON_CANCEL = "Cancel"

        private const val FALLBACK_MESSAGE = """Could not open JBR selection dialog automatically.

Please go to: Help → Find Action → type "Choose Runtime" and select a JetBrains Runtime with JCEF support."""
    }

    /**
     * The method is expected to run fast.
     *
     * @param file file to be tested for acceptance.
     * @return `true` if provider can create valid editor for the specified `file`.
     */
    override fun accept(project: Project, file: VirtualFile): Boolean {
        // Only process SVGA files
        if (file.fileType !is SvgaFileType) {
            return false
        }

        // Check if JCEF is available
        if (!JBCefApp.isSupported()) {
            // Show warning dialog only once per session
            if (jcefWarningShown.compareAndSet(false, true)) {
                showJcefWarningDialog()
            }
            return true
        }

        return true
    }

    /**
     * Shows a warning dialog informing the user that JCEF is not available.
     * Provides a "Change JBR" button to open the JBR selection dialog.
     * Must be called on EDT.
     */
    private fun showJcefWarningDialog() {
        // Ensure dialog is shown on EDT
        ApplicationManager.getApplication().invokeLater {
            val result = Messages.showOkCancelDialog(
                DIALOG_MESSAGE, DIALOG_TITLE, BUTTON_CHANGE_JBR, BUTTON_CANCEL, Messages.getWarningIcon()
            )

            if (result == Messages.OK) {
                openChooseBootRuntimeDialog()
            }
        }
    }

    /**
     * Opens the "Choose Boot Java Runtime for the IDE" dialog.
     * Tries multiple possible action IDs as different IDE versions may use different IDs.
     * Uses ActionUtil.invokeAction() to properly invoke the action without violating API contracts.
     * Shows a fallback message if no action is found.
     */
    private fun openChooseBootRuntimeDialog() {
        val actionManager = ActionManager.getInstance()

        // Try each possible action ID
        for (actionId in CHOOSE_RUNTIME_ACTION_IDS) {
            val action = actionManager.getAction(actionId)
            if (action != null) {
                LOG.info("Found Choose Runtime action with ID: $actionId")

                // Get the IDE frame as the component context for the action
                val frame = WindowManager.getInstance().findVisibleFrame()
                if (frame != null) {
                    // Use ActionUtil.invokeAction which is the recommended way to execute actions
                    // Parameters: action, component, place, inputEvent, onDone
                    ActionUtil.invokeAction(
                        action, frame.rootPane, ActionPlaces.UNKNOWN, null, null
                    )
                    return
                }
            }
        }

        // No action found or no frame available, show fallback message
        LOG.warn("No Choose Runtime action found or no frame available. Tried: ${CHOOSE_RUNTIME_ACTION_IDS.contentToString()}")
        Messages.showInfoMessage(FALLBACK_MESSAGE, "Manual Action Required")
    }

    /**
     * Creates editor for the specified file.
     *
     *
     * This method is called only if the provider has accepted this file (i.e. method [.accept] returned
     * `true`).
     * The provider should return only valid editor.
     *
     * @return created editor for specified file.
     */
    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        return SvgaFileEditorImpl(file)
    }

    /**
     * @return editor type ID for the editors created with this FileEditorProvider. Each FileEditorProvider should have
     * a unique nonnull ID. The ID is used for saving/loading of EditorStates.
     */
    override fun getEditorTypeId(): String {
        return "svga"
    }

    /**
     * @return a policy that specifies how an editor created via this provider should be opened.
     * @see FileEditorPolicy.NONE
     *
     * @see FileEditorPolicy.HIDE_DEFAULT_EDITOR
     *
     * @see FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR
     *
     * @see FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR
     */
    override fun getPolicy(): FileEditorPolicy {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR
    }
}
