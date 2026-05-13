## ADDED Requirements

### Requirement: JCEF availability detection
The system SHALL check JCEF availability using `JBCefApp.isSupported()` when a user attempts to open an SVGA file.

#### Scenario: JCEF is available
- **WHEN** user opens an SVGA file and `JBCefApp.isSupported()` returns `true`
- **THEN** the system SHALL proceed to create the SVGA editor normally

#### Scenario: JCEF is not available
- **WHEN** user opens an SVGA file and `JBCefApp.isSupported()` returns `false`
- **THEN** the system SHALL display a warning dialog and return `false` from `accept()` to prevent editor creation

### Requirement: Warning dialog display
The system SHALL display an English warning dialog when JCEF is not available, containing:
- Title: "JCEF Not Available"
- Message explaining that SVGA preview requires JCEF and guiding user to switch JBR
- Two buttons: "Change JBR" and "Cancel"

#### Scenario: Dialog content and layout
- **WHEN** the warning dialog is displayed
- **THEN** the dialog SHALL show a warning icon, English title "JCEF Not Available", and message text explaining the issue

#### Scenario: Dialog appears only once per session
- **WHEN** user opens multiple SVGA files in a single IDE session with JCEF unavailable
- **THEN** the dialog SHALL appear only once (first time), subsequent attempts SHALL silently return `false`

### Requirement: Change JBR button action
The system SHALL provide a "Change JBR" button that opens the "Choose Boot Java Runtime for the IDE" dialog.

#### Scenario: User clicks Change JBR button
- **WHEN** user clicks the "Change JBR" button
- **THEN** the system SHALL invoke `ChooseBootJavaRuntimeAction` to open the JBR selection dialog

#### Scenario: ChooseBootJavaRuntimeAction not available
- **WHEN** user clicks "Change JBR" but `ChooseBootJavaRuntimeAction` is not found in ActionManager
- **THEN** the system SHALL gracefully handle the missing action without crashing

### Requirement: Cancel button action
The system SHALL provide a "Cancel" button that closes the dialog without further action.

#### Scenario: User clicks Cancel button
- **WHEN** user clicks the "Cancel" button
- **THEN** the dialog SHALL close and the system SHALL return `false` from `accept()`

### Requirement: Thread safety
The system SHALL ensure the dialog is displayed on the Event Dispatch Thread (EDT).

#### Scenario: accept() called from background thread
- **WHEN** `accept()` is called from a non-EDT thread and JCEF is not available
- **THEN** the system SHALL use `ApplicationManager.getApplication().invokeLater()` to display the dialog on EDT
