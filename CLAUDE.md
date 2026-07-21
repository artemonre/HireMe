# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

Run from the repo root using the Gradle wrapper (`./gradlew` / `gradlew.bat`).

### Run
- Android app: `./gradlew :app:androidApp:assembleDebug`
- Desktop app (hot reload): `./gradlew :app:desktopApp:hotRun --auto`
- Desktop app (standard): `./gradlew :app:desktopApp:run`
- Server: `./gradlew :server:run`
- Web app, Wasm target (faster, modern browsers): `./gradlew :app:webApp:wasmJsBrowserDevelopmentRun`
- Web app, JS target (slower, supports older browsers): `./gradlew :app:webApp:jsBrowserDevelopmentRun`
- iOS app: open `app/iosApp` in Xcode and run from there (not a Gradle target).

### Test
- Android tests: `./gradlew :app:shared:testAndroidHostTest`
- Desktop tests: `./gradlew :app:shared:jvmTest`
- Server tests: `./gradlew :server:test`
- Web tests (Wasm): `./gradlew :app:shared:wasmJsTest`
- Web tests (JS): `./gradlew :app:shared:jsTest`
- iOS tests: `./gradlew :app:shared:iosSimulatorArm64Test`

Run a single test class with `--tests`, e.g. `./gradlew :server:test --tests "com.artemonre.hireme.ApplicationTest"`.

## Architecture

This is a Kotlin Multiplatform project with a Gradle module layout, targeting Android, iOS, Desktop (JVM), Web (JS/Wasm), and a Ktor server:

- **`:core`** — Platform-agnostic Kotlin (no Compose dependency), built for all targets (Android/iOS/JVM/JS/Wasm). Holds business logic shared by *both* the app and the server. This is where new shared, non-UI logic should go.
- **`:app:shared`** — Compose Multiplatform UI and app logic shared across all app targets. Depends on `:core` (`api(project(":core"))`). Platform differences are handled via Kotlin's `expect`/`actual` mechanism: `Platform.kt` declares `expect fun getPlatform(): Platform`, with `actual` implementations per source set (`androidMain/Platform.android.kt`, `iosMain/Platform.ios.kt`, `jvmMain/Platform.jvm.kt`, `jsMain/Platform.js.kt`, `wasmJsMain/Platform.wasmJs.kt`). Follow this pattern for any new platform-specific behavior.
- **`:app:androidApp`**, **`:app:desktopApp`**, **`:app:webApp`** — thin per-platform entry points that depend on `:app:shared` and launch the shared Compose UI (`App()`).
- **`app/iosApp`** — a plain Xcode project, not a Gradle module. `MainViewController.kt` (in `:app:shared`'s `iosMain`) exposes the shared Compose UI as a `UIViewController`; the SwiftUI entry point in `iosApp/iosApp.swift` hosts it. Edit Swift-side code directly in Xcode.
- **`:server`** — Ktor (Netty) backend. Depends only on `:core`, not `:app:shared` — it has no UI dependency and shares only business logic with the apps.

Dependency direction: `:server` and `:app:androidApp`/`:app:desktopApp`/`:app:webApp` → `:app:shared` → `:core`, with `:server` depending on `:core` directly (bypassing `:app:shared`).

Gradle plugin/dependency versions are centralized in `gradle/libs.versions.toml` (version catalog); add new dependencies there rather than hardcoding versions in module `build.gradle.kts` files.

## Git workflow

- Feature branches merge into `develop` by default, not `master`. Only merge into `master` when explicitly told to.

## Coding guidelines

- Prefer non-deprecated functions and classes. If the only option is deprecated, or the deprecated one is genuinely the better choice, ask before using it rather than picking silently.
- Avoid hardcoded resources (colors, strings, dimensions, etc.) — create or reuse a shared resource (theme color, string resource, dimension constant, ...) where possible instead of inlining a literal. When planning work and a hardcoded value looks necessary, call it out explicitly in the plan rather than letting it pass silently.
