# Progress

Living status record. Update it at the end of every working session. Every claim
needs evidence; anything not run is labelled NOT RUN.

## Session record

**Date/session:** 2026-10-29 to 2026-10-31, session 1
**Current phase:** P1, repository and reproducible shell (scaffold built, to be merged)

### Done and verified

- T-0001 to T-0006: environment, toolchain baseline, repository name, application ID,
  repository creation. See `docs/DECISIONS.md` (D-001 to D-004). Pushing to GitHub works.
- T-0007: project records committed: `README.md`, `.gitattributes`, `.gitignore`,
  `docs/PROGRESS.md`, `docs/DECISIONS.md`, `docs/MASTER_PROJECT_PROMPT.md`.
  `LICENSE` (Apache-2.0) was created by GitHub.
- T-0008: Android scaffold committed on branch `feat/android-scaffold`, one file per
  commit: Gradle wrapper pinned to 9.6.0 with distribution checksum (jar SHA-256
  matches Gradle's published value), `settings.gradle.kts` with the optional mirror
  switch (D-005), version catalog, root build file, `gradle.properties`, app module
  build file, manifest, string and theme resources, `MainActivity.kt`, and one
  placeholder unit test.
- T-0009 (first local build), run by the maintainer on 2026-10-31 with the mirror
  switch on:
  - `gradlew.bat :app:assembleDebug`: BUILD SUCCESSFUL in 6m 2s, 36 tasks, producing
    `app-debug.apk` (11,446,626 bytes).
  - `gradlew.bat :app:testDebugUnitTest`: BUILD SUCCESSFUL; report shows tests=1,
    failures=0, errors=0.
  - `gradlew.bat :app:lintDebug`: BUILD SUCCESSFUL; 0 errors, 7 warnings (see below).
  - Installed with `adb install` and started on an Honor 8C (Android 8.1, API 27,
    arm64-v8a): installation Success, process still running after 4 seconds, crash
    log empty, and the screen showed the app name.

### In progress

- Close T-0008: open a pull request from `feat/android-scaffold` to `main`, review the
  diff and merge.
- Other records from the master prompt (TASKS, SPEC, PLAN, TEST_STRATEGY, SECURITY,
  RELEASE, OPERATIONS, CONTRIBUTING, CHANGELOG, AGENTS) will be created when they
  carry real content.

### Blocked / decisions needed

- Later decisions: theme default, adaptive options, performance budgets, release
  channels, launcher icon, backup and data-extraction rules.

### Failed / not run

- FAILED: `sdkmanager --list` could not download its package lists, so Android SDK
  components cannot be installed with `sdkmanager` from the maintainer's network.
- FAILED: three Google Maven metadata URLs returned 404 from the maintainer's
  network. Libraries come from a third-party Maven mirror instead.
- NOT RUN: any build with the mirror switch off. That needs a network that reaches
  Google Maven, planned through GitHub Actions (T-0010).
- NOT RUN: any check on API 23 (device or emulator). The only device is API 27.
- NOT RUN: instrumented tests, release build, accessibility checks, dependency
  verification metadata.
- NOT CONFIRMED: how the README and docs render on GitHub. The maintainer has not
  reported a visual check.

### Accepted lint warnings (lint run on 2026-10-31, 0 errors, 7 warnings)

- OldTargetApi and GradleDependency: targetSdk and compileSdk are 36 while 37 exists.
  Accepted under D-003.
- NewerVersionAvailable: Kotlin 2.4.21 exists. The project stays on Kotlin 2.2.10,
  the version AGP 9.5.0 depends on (D-004).
- AndroidGradlePluginVersion: Gradle 9.8.2 exists. The wrapper stays on 9.6.0, the
  minimum AGP 9.5.0 requires. Review as part of a toolchain refresh.
- ObsoleteLintCustomCheck: lifecycle-runtime's lint checks are skipped
  (UnsafeLifecycleWhenUsage, RepeatOnLifecycleWrongUsage). The app does not use the
  APIs they check. Revisit when lifecycle is updated.
- DataExtractionRules: the manifest sets allowBackup="false". Decide on backup and
  data-extraction rules when favorites storage exists.
- MissingApplicationIcon: no launcher icon yet. To be scheduled as a branding task.

### Risks / assumptions changed

- SDK platforms and emulator images come from offline packages that cannot be
  compared with Google's official checksums. Record file hashes for anything used.
- The build depends on a third-party library mirror, which affects reproducibility
  for other contributors. The mirror switch is off by default.
- minSdk 23 is a requirement but is not verified on any Android 6.0 device.
- The toolchain is pinned below the newest versions (compileSdk, Kotlin, Gradle).
  Moving up is a deliberate future task.

### Evidence still missing

- A build with the mirror switch off (GitHub Actions).
- A device or emulator running API 23.
- Checksums of SDK packages in use.

### Next single task

Merge `feat/android-scaffold` into `main` through a GitHub pull request. Acceptance:
the pull request shows only the scaffold files and documentation updates, and after
merging `main` builds the same way. Then T-0010, continuous integration.
