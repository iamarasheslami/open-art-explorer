# Progress

Living status record. Update it at the end of every working session. Every claim
needs evidence; anything not run is labelled NOT RUN.

## Session record

**Date/session:** 2026-10-29, session 1
**Current phase:** P1, repository and reproducible shell (in progress)

### Done and verified

- T-0001: Development environment is Windows 11, VS Code and PowerShell.
  Evidence: stated by the maintainer.
- T-0002: Environment inventory collected from commands run on the maintainer's PC.
  Git 2.55.0.windows.2, JDK 21.0.11 with JAVA_HOME set, global Gradle 8.13,
  ANDROID_HOME set, adb 37.0.0, Build Tools 36.0.0, 36.1.0 and 37.0.0,
  SDK platforms up to API 36.
- T-0003: Provisional toolchain baseline approved, to be proven by the first clean
  build (T-0009): Gradle wrapper 9.6.0, Android Gradle Plugin 9.5.0, Build Tools
  36.0.0 or newer, compileSdk 36, minSdk 23, targetSdk 36, Compose BOM 2026.06.01
  (Compose UI 1.11.4), Kotlin candidate 2.3.21. Evidence: official AGP 9.5.0
  release notes (read 2026-10-29) and file checks against the library mirror.
- T-0004: Repository name `open-art-explorer`, public.
- T-0005: Application ID `io.github.iamarasheslami.openartexplorer`.
- T-0006: Repository created and cloned. Commit identity set for this repository.
- T-0007 (partly done): `.gitignore`, `.gitattributes` and `README.md` committed
  and pushed (commits fc7e7fa, 2ffa1d5, e48f89d).

### In progress

- T-0007: remaining project documents, one file per step. Next action: finish the
  project records under `docs/`.

### Blocked / decisions needed

- Mirror policy: whether library mirror settings are committed in the repository
  or kept in a personal Gradle init script. To be decided and recorded as an ADR
  before T-0008.
- Later decisions: theme default, adaptive options, performance budgets, release
  channels.

### Failed / not run

- FAILED: `sdkmanager --list` could not download its package lists, so Android SDK
  components cannot be installed with `sdkmanager` from the maintainer's network.
- FAILED: three Google Maven metadata URLs returned 404 from the maintainer's
  network. Libraries come from a third-party Maven mirror instead.
- NOT RUN: any Gradle build, test or lint. There is no application code yet.
- NOT RUN: any check on an API 23 emulator (no image available yet).

### Risks / assumptions changed

- SDK platforms and emulator images come from offline packages that cannot be
  compared with Google's official checksums. Record file hashes for anything used.
- The build depends on a third-party library mirror, which affects reproducibility
  for other contributors.
- minSdk 23 is a requirement but is not yet verified on any Android 6.0 device.
- Kotlin and Compose version compatibility is unproven until the first build.

### Evidence still missing

- Android version of the test phone (Honor 8C).
- First clean local build (T-0009) with exact commands and output.
- Checksums of SDK packages in use.

### Next single task

T-0007, next file: `docs/MASTER_PROJECT_PROMPT.md` (the canonical copy of the
project's working instructions). Acceptance: the file is committed and pushed, and
`git status` is clean.
