## 1. Core Implementation

- [x] 1.1 Add `AtomicBoolean` flag to `SvgaFileEditorProvider` for dialog-shown tracking
- [x] 1.2 Implement JCEF availability check logic in `accept()` method
- [x] 1.3 Create warning dialog using `Messages.showOkCancelDialog()` with "Change JBR" and "Cancel" buttons
- [x] 1.4 Implement `ChooseBootJavaRuntimeAction` invocation when user clicks "Change JBR"
- [x] 1.5 Ensure dialog displays on EDT using `ApplicationManager.getApplication().invokeLater()`
- [x] 1.6 Return `false` from `accept()` when JCEF is not available

## 2. Error Handling

- [x] 2.1 Add null check for `ActionManager.getAction("ChooseBootJavaRuntimeAction")`
- [x] 2.2 Handle gracefully if action is not found (log warning, skip action)

## 3. Build Verification

- [x] 3.1 Run `./gradlew clean buildPlugin` and verify build success

## 4. Manual Testing (User)

- [ ] 4.1 Test with non-JBR runtime to verify dialog appears
- [ ] 4.2 Verify "Change JBR" button opens JBR selection dialog
- [ ] 4.3 Verify "Cancel" button closes dialog without action
- [ ] 4.4 Verify dialog appears only once per IDE session
- [ ] 4.5 Test with JBR runtime to verify normal SVGA preview works
