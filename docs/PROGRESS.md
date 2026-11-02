# Progress

Living status record. Update it at the end of every working session. Every claim
needs evidence; anything not run is labelled NOT RUN.

## Session record

**Date/session:** 2026-10-29 to 2026-11-01, session 1
**Current phase:** P2, specification and architecture (documents complete, in review)

### Done and verified

- T-0001 to T-0006: environment, toolchain baseline, repository name, application ID,
  repository creation. See `docs/DECISIONS.md` (D-001 to D-004). Pushing to GitHub works.
- T-0007: project records committed: `README.md`, `.gitattributes`, `.gitignore`,
  `docs/PROGRESS.md`, `docs/DECISIONS.md`, `docs/MASTER_PROJECT_PROMPT.md`.
  `LICENSE` (Apache-2.0) was created by GitHub.
- T-0008: Android scaffold merged into `main` through pull request #1.
- T-0009 (first local build), run by the maintainer on 2026-10-31 with the mirror
  switch on: `assembleDebug` BUILD SUCCESSFUL in 6m 2s (APK 11,446,626 bytes), unit
  test report tests=1 failures=0, lint 0 errors and 7 warnings, and an install and
  start on an Honor 8C (Android 8.1, API 27) with an empty crash log.
- T-0010: GitHub Actions workflow merged (pull request #2, D-009). Runs on `main`
  after the merges were green (57 s and 2 min 34 s).
- Documentation pull request #3 (D-009, status page) merged.
- Checks made for phase P2: the museum's API documentation and terms were read; from
  the maintainer's network the new search endpoint answered, the old one answered
  HTTP 410, and a sample record had the expected fields; 15 of 15 candidate libraries
  were reachable (mirror or Maven Central). Official notes were read for Android 16
  behavior changes, Room 3, Navigation 3 and the splash screen library.

### In progress

- T-0011 and T-0012 (one pull request, `docs/spec-provider-rights`): `docs/SPEC.md`
  (34 requirements in groups A to D, decisions and open questions),
  `docs/TEST_STRATEGY.md`, `docs/ARCHITECTURE.md`, `docs/adr/0001` to `0006`,
  `docs/DESIGN.md`, and decisions D-010 and D-011. Approval is given by merging the
  pull request.
- Other records from the master prompt (TASKS, SECURITY, RELEASE, OPERATIONS,
  CONTRIBUTING, CHANGELOG, AGENTS) will be created when they carry real content.

### Blocked / decisions needed

- None for the maintainer after the pull request is merged.
- Decided by short tests later: OQ-07 (blur technique and low-end default), OQ-08 (start
  screen ceiling and start theme technique), OQ-09 (cache sizes).

### Failed / not run

- FAILED: `sdkmanager --list` could not download its package lists, so Android SDK
  components cannot be installed with `sdkmanager` from the maintainer's network.
- FAILED: three Google Maven metadata URLs returned 404 from the maintainer's
  network. Libraries come from a third-party Maven mirror instead.
- NOT RUN: any check on API 23 (device or emulator). The only device is API 27.
- NOT RUN: all tests listed in `docs/TEST_STRATEGY.md` except the placeholder unit test;
  instrumented tests, release build, accessibility checks, dependency verification
  metadata.
- NOT CONFIRMED: how the README and docs render on GitHub, and the CI run logs.

### Accepted lint warnings (lint run on 2026-10-31, 0 errors, 7 warnings)

- OldTargetApi and GradleDependency: targetSdk and compileSdk are 36 while 37 exists
  (D-003).
- NewerVersionAvailable: Kotlin 2.4.21 exists. The project stays on Kotlin 2.2.10, the
  version AGP 9.5.0 depends on (D-004).
- AndroidGradlePluginVersion: Gradle 9.8.2 exists. The wrapper stays on 9.6.0, the
  minimum AGP 9.5.0 requires. Review in a toolchain refresh.
- ObsoleteLintCustomCheck: lifecycle-runtime's lint checks are skipped. The app does
  not use the APIs they check.
- DataExtractionRules: backup and data-extraction rules are decided when favorites
  storage exists.
- MissingApplicationIcon: scheduled as FR-BRD-001.

### Risks / assumptions changed

- SDK platforms and emulator images come from offline packages that cannot be
  compared with Google's official checksums. Record file hashes for anything used.
- The local build depends on a third-party library mirror. CI builds without it. A
  green build shows that both sets of files work, not that they are identical.
- minSdk 23 is a requirement but is not verified on any Android 6.0 device.
- Library versions for the planned stack are not pinned yet. Each is pinned when first
  added, and some newer releases may need compileSdk 37.
- The start screen flow and the glass blur technique are unproven until their short
  tests are done.
- The museum may restrict or end its API, or revise its terms.

### Evidence still missing

- Dependency verification metadata (checksums) to compare mirror and official files.
- A device or emulator running API 23.
- Checksums of SDK packages in use.
- Measured performance baseline on the Honor 8C.

### Next single task

T-0013: implement the provider contract and the Met adapter with their tests, as the
first code slice (phase P3). Acceptance: fixtures from real records, mapper and rights
policy tests with positive and negative cases, request-building contract tests, and CI
green.
