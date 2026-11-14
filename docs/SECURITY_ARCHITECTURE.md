# Security architecture and trust boundaries

## What this prototype actually protects

- The app declares no runtime permissions and includes no network permission. It does not collect contacts, photos, files, location, microphone, or camera data.
- AndroidX `BiometricPrompt` requests authentication from the host Android biometric subsystem when the user explicitly long-presses the preview lock screen's fingerprint affordance. A successful prompt changes only the prototype's screen. It does **not** unlock or replace the device keyguard.
- No passwords, biometric templates, cryptographic keys, or protected user data are persisted. The app has no reason to create a Keystore key.
- The manifest disables Android app-data backup for this sample. The app remains subject to Android's normal app sandbox and platform permission enforcement.
- Security-center lock status and `Build.VERSION.SECURITY_PATCH` are reported by the running Android image; they are informational, not cryptographic validation.

## What is simulated

The in-app lock screen, notifications and their history, recents, settings, security event, status-bar details, Quick Settings toggles, brightness, and volume are sample UI/state. They do not enforce security or alter Android system services. The sample security event is not a privileged audit log. App placeholders do not access their named hardware or user data.

## What remains the host Android's responsibility

The installed Android build—not this app—provides Verified Boot, SELinux policy, app process isolation, platform permission enforcement, credential verification, file-based encryption, hardware-backed Keystore, system update signing and rollback behavior, keyguard enforcement, and trusted security-event sources. A launcher or app UI cannot independently establish or attest these guarantees.

## Before expanding app-level data handling

Keep sensitive operations behind Android APIs and least-privilege runtime permissions. Explain a permission in context immediately before requesting it. Use Android Keystore-backed keys for any future protected local data and established authenticated-encryption APIs; never persist raw credentials, biometric material, or key bytes. Validate backup/export behavior, lifecycle/cancellation handling, and clear failure reporting with tests before shipping. This preview intentionally has no such data path.
