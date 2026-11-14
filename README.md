# Serein — Android interface prototype

Serein is an original, app-level Android interface concept built with Kotlin and Jetpack Compose. It is a runnable **application prototype**, not a custom Android OS, launcher replacement, or security product.

## Open and run

1. Install Android Studio with Android SDK Platform 35 and a JDK 17 runtime.
2. Open this folder in Android Studio and allow Gradle to sync. The first sync needs access to Google's and Maven Central's dependency repositories.
3. Create or start an API 26+ Android emulator (API 35 recommended), then select **Run** for the `app` configuration.
4. To build from a terminal with Gradle installed, run `gradle :app:assembleDebug`. No Gradle wrapper is checked in.

The current task environment could not start PowerShell, so Java, Gradle, the Android SDK, emulator, and build status could not be verified here. No APK has been produced or tested in an emulator.

## Project map

- `app/src/main/java/dev/serein/prototype/MainActivity.kt` — Android activity and platform biometric prompt.
- `app/src/main/java/dev/serein/prototype/ui/PhonePrototypeApp.kt` — preview screens, interactions, and demo state.
- `app/src/main/java/dev/serein/prototype/ui/DesignSystem.kt` — Compose color schemes and contrast adjustment.
- `docs/DESIGN_SYSTEM.md` — visual and motion decisions.
- `docs/SECURITY_ARCHITECTURE.md` — prototype-vs-platform security boundary.
- `docs/AOSP_ROADMAP.md` — staged OS engineering and release-validation plan.

## Implemented

- First-run welcome with light/dark selection and an explicit privacy explanation.
- Simulated lock-screen view; optional biometric prompt uses AndroidX `BiometricPrompt` and never reads or stores biometric data.
- Two-page home app grid, long-press/drag-to-reorder gestures, customization mode, widget-style card, and dock.
- Alphabetical app drawer with search; dismissible, expandable sample notifications; recent-app cards.
- Quick-settings-style controls with editable tile order and brightness/volume sliders. All are in-app demo state and do not change device settings.
- Settings search, theme and accent choices, larger text, high contrast, reduce-motion, and reduce-transparency preferences for the current app session.
- Security center showing host Android screen-lock configuration and reported security-patch level, plus clear demo-only disclosures.
- Labeled Phone, Messages, Camera, Photos, Files, Clock, Calendar, Browser, Settings, and Security preview apps.
- No runtime permissions, credentials, protected local data, or encryption keys are requested or stored.

## Known limitations

- The “lock screen,” home, shade, quick settings, and recents are screens inside an ordinary app. They do not replace Android System UI, become the device's keyguard, enforce an unlock, or control radios, brightness, volume, notifications, or other apps.
- Most app icons lead to labeled informational preview cards; there is no telephony, messaging, camera, gallery, file, calendar, browser, or system-settings integration.
- Theme and accessibility choices are held in memory and reset when the app process is recreated. There is no wallpaper picker, folder implementation, real widget host, system notification listener, drag-between-pages placement, or AOSP build.
- A secure lock and update patch string are observations reported by the host Android image. Their presence does not verify the integrity, freshness, or provenance of that image.
- No build or emulator execution could be performed in this environment.

See the [design system](docs/DESIGN_SYSTEM.md), [security note](docs/SECURITY_ARCHITECTURE.md), and [AOSP roadmap](docs/AOSP_ROADMAP.md).
