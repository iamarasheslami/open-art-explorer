# Decisions

Register of material project decisions. Each entry records what was decided, the
options considered, why, who approved it, the consequences and when to revisit it.
Status is one of: Approved, Provisional (approved but not yet proven), Pending.

Maintainer and approver: Arash Eslami (@iamarasheslami).

## D-001: Repository name and visibility

- **Date / status:** 2026-10-29, Approved
- **Decision:** The repository is named `open-art-explorer` and is public.
- **Options considered:** other names; a private repository.
- **Reason / trade-offs:** The name matches the project and follows common
  lowercase-with-hyphens style. Public source fits an open-source goal, but history
  is visible to everyone and hard to clean afterwards.
- **Consequences:** Never commit secrets or personal files. `.gitignore` is the
  first file added to guard against this.
- **Revisit when:** a rename is wanted, ideally before the first release.

## D-002: Android application ID

- **Date / status:** 2026-10-29, Approved
- **Decision:** `io.github.iamarasheslami.openartexplorer`
- **Options considered:** `org.openartexplorer.app` and `com.<name>.openartexplorer`.
  Both imply ownership of a web domain that does not exist.
- **Reason / trade-offs:** It is tied to the GitHub account that owns the project,
  so it is unique without claiming a domain. It is longer than the alternatives.
- **Consequences:** The ID cannot change after release without becoming a different
  app. The code package should match it.
- **Revisit when:** only before the first release.

## D-003: compileSdk 36 with the Compose 1.11 line (Option A)

- **Date / status:** 2026-10-29, Approved
- **Decision:** Build against Android SDK platform 36 and the Compose BOM
  2026.06.01 (Compose UI 1.11.4).
- **Options considered:** Option B, compileSdk 37 with Compose 1.12 or newer.
  Compose 1.12 requires compileSdk 37 and Android Gradle Plugin 9.
- **Reason / trade-offs:** Platform 36 is already installed. Platform 37 would have to
  come from an unofficial source whose integrity cannot be checked against
  official checksums. The cost is a slightly older Compose line.
- **Consequences:** Newest Compose features and fixes are not available until we move.
- **Revisit when:** a trustworthy platform 37 package is available, or the Compose
  1.11 line stops receiving fixes.

## D-004: Provisional toolchain baseline

- **Date / status:** 2026-10-29, Provisional until the first clean build (T-0009)
- **Decision:** JDK 21, Gradle wrapper 9.6.0, Android Gradle Plugin 9.5.0, Build Tools
  36.0.0 or newer, minSdk 23, compileSdk 36, targetSdk 36, Kotlin candidate 2.3.21.
- **Options considered:** other versions of each component.
- **Reason / trade-offs:** Chosen from the official AGP 9.5.0 requirements (read
  2026-10-29) and from file checks against the library mirror. Compatibility on paper
  is not proof that it builds.
- **Consequences:** If the first build fails, adjust one component at a time and
  record the change here.
- **Revisit when:** the first clean build runs, or a component version changes.

## D-005: Library mirror

- **Date / status:** 2026-10-29, Approved; implementation to be proven in T-0008
- **Decision:** Libraries may be fetched through `maven.myket.ir`, enabled by an
  optional switch. `settings.gradle.kts` adds the mirror first only when a personal
  Gradle property turns it on. It is off by default, so contributors and CI use
  Google Maven and Maven Central directly. The mirror `maven.devneeds.ir` is excluded
  because on 2026-10-29 it returned an HTML "restricted access" page instead of
  Maven files.
- **Options considered:** committing the mirror unconditionally; a personal Gradle init
  script outside the repository; Google Maven directly (not reachable from the
  maintainer's network).
- **Reason / trade-offs:** The mirror served the Android Gradle Plugin 9.5.0 files and
  the Compose BOM on 2026-10-29. An optional switch keeps the maintainer's workflow
  without making the public project depend on a third party, and lets CI build
  against official sources as an independent integrity check. The cost is a small
  block in the committed settings file and one property to set once per machine.
- **Consequences:** Dependency verification (checksums) stays enabled. The switch and
  both modes are tested in the Android scaffold step (T-0008). Until then this is a
  design, not a proven setup.
- **Revisit when:** the mirror stops serving files, or the switch proves unreliable.files, or contributors need to build.

## D-006: Test devices

- **Date / status:** 2026-10-29, Approved
- **Decision:** Use a physical Honor 8C as the only test device for now. API 23
  emulator testing is deferred.
- **Reason / trade-offs:** Only an Android 12 (API 31) emulator image is available,
  and other images cannot be fetched from the official source from the
  maintainer's network.
- **Consequences:** The minSdk 23 requirement is NOT VERIFIED at API 23. This stays
  recorded as a known gap and risk.
- **Revisit when:** an API 23 image can be obtained, and in any case before release.

## D-007: Publish the master prompt in this repository

- **Date / status:** 2026-10-29, Approved
- **Decision:** `docs/MASTER_PROJECT_PROMPT.md` contains the full v1.1 text unchanged
  and is the canonical copy.
- **Options considered:** keep the prompt private with only a pointer here; publish an
  edited version.
- **Reason / trade-offs:** The document contains no secrets. It mentions the test
  phone and a network constraint. One canonical copy avoids two documents drifting
  apart.
- **Revisit when:** the contract changes (bump the version) or the content is
  reconsidered for privacy.

## D-008: Android scaffold approach and branch workflow

- **Date / status:** 2026-10-29, Approved
- **Decision:** Build the Android scaffold from hand-written, reviewed files, with the
  Gradle wrapper created by the official `gradle wrapper` command. Work happens on
  the branch `feat/android-scaffold`, one file per commit with conventional commit
  messages. The branch merges into `main` through GitHub after the first clean build
  (T-0009) passes.
- **Options considered:** the Android Studio New Project template (bulk generated,
  unreviewed, needs Google servers); every file by hand (the Gradle wrapper contains a
  binary file that cannot be pasted).
- **Reason / trade-offs:** Every file is reviewed, and `main` never holds a half-finished
  project that cannot build. The cost is about nine small steps before the first build.
- **Consequences:** The scaffold contains no features. File syntax is checked against
  current official documentation before each file is written.
- **Revisit when:** the first build fails for reasons that point to the scaffold
  approach itself.
