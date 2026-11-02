# Test strategy

Status: Approved with the phase P2 pull request. Nothing here has been run yet. Every
row is NOT RUN until evidence is recorded in `docs/PROGRESS.md`.

## Principles

- Verify behavior, not wiring: tests assert outcomes that users and the provider
  contract care about.
- Fakes over mocks. A fake provider and an in-memory database replace the network and
  the disk.
- Time, randomness and dispatchers are injected, so tests are deterministic.
- Real provider records, saved as fixtures with their source and date, drive the
  mapper and rights tests.
- Every requirement has at least one positive and one negative check, automated or
  manual and named.
- A failing test is investigated. It is never deleted or weakened to pass.
- Live API calls are never part of the default test run.

## Layers

| Layer | What it covers | Tools (versions pinned when first used) | In CI |
| --- | --- | --- | --- |
| Unit (JVM) | Mappers, rights policy, request limiter, paging, refresh policy, settings logic, discovery sampling | JUnit 4, kotlinx-coroutines-test, Turbine | Yes |
| Adapter contract (JVM) | Request building and response parsing against a local fake server | OkHttp MockWebServer | Yes |
| Repository and storage (JVM) | Paging, caching, refresh, favorites, saved files, with an in-memory Room 3 database | JUnit 4; technique proven in the first storage slice | Yes |
| UI state (JVM) | ViewModels produce the expected state for every case | JUnit 4, Turbine | Yes |
| UI (instrumented) | Navigation, gallery states, settings, accessibility semantics | Compose UI tests on a device or emulator | Later, needs an emulator job |
| Static checks | Lint, manifest and dependency review | Android lint | Yes |
| Live smoke (manual) | A short check against the real API before a release | PowerShell | No |
| Performance (manual) | Baseline on the Honor 8C and an emulator | Profiling tools | No |

## Fixtures

Directory plan: `app/src/test/resources/fixtures/met/`, with an index file that records
the source address and the date each fixture was saved. Cases:

- An eligible record with a thumbnail and a large image.
- A record that is not public domain.
- A record with the flag true but no image link.
- Empty strings, missing fields, and a field of an unexpected type.
- A record with additional images.
- Search results with totals of 0, 153 and 12,345.
- Error answers: not found, retired endpoint, and a server error.

## Traceability

| Requirement | Primary verification | Level |
| --- | --- | --- |
| DATA-API-001 | Request-building contract test | Adapter contract |
| DATA-API-002 | Limiter test with a controllable clock; measurement report | Unit, performance |
| DATA-SRC-001 | Mapper tests with incomplete records | Unit |
| DATA-RGT-001 | Positive and negative tests of the rights policy | Unit |
| DATA-RGT-002 | Fake provider that changes its answer | Repository |
| NFR-PRIV-002 | Documentation review in the release checklist | Manual |
| FR-START-001 | Screen recordings on two device classes; readiness test | Manual, unit |
| FR-NAV-001 | Navigation state tests | UI |
| FR-GAL-001 | Mapper, repository and state tests | Unit, repository |
| FR-DISC-001 | Seeded random sampling test | Unit |
| FR-SEA-001 | Contract test; paging test | Adapter contract, repository |
| FR-SEA-002 | Fixture totals of 12,345 and 153 | Repository, UI |
| FR-FLT-001 | Contract test; date-range validation test | Adapter contract, unit |
| FR-DET-001 | Mapper and state tests | Unit |
| FR-IMG-001 | Gesture test; manual check on the Honor 8C | UI, manual |
| FR-FAV-001 | Database and repository tests | Repository |
| FR-OFF-001 | Repository tests in a simulated offline state | Repository |
| DATA-REF-001 | Repository tests with a controllable clock | Repository |
| FR-DL-001 | File lifecycle tests; manual sharing check | Repository, manual |
| DATA-RGT-003 | Fake provider and file tests | Repository |
| FR-THE-001 | Preference tests; recording of the start with a mismatched theme | Unit, manual |
| FR-UIX-001 | Screenshots on both device classes; contrast check | Manual |
| FR-ADA-001 | Request-count test with a fake provider | Repository |
| FR-SET-001 | Preference and settings screen tests | Unit, UI |
| FR-BRD-001 | Visual review in several launcher shapes; lint | Manual, static |
| NFR-LAY-001 | UI tests with window size variations | UI |
| NFR-ACC-001 | Accessibility checklist and manual TalkBack review | Manual |
| NFR-PRIV-001 | Manifest and dependency review | Static |
| NFR-SEC-001 | Mapper tests with unexpected addresses | Unit |
| NFR-RES-001 | Negative tests for every failure state | Unit, UI |
| NFR-MOD-001 | Dependency direction check | Static |
| NFR-PERF-001 | Baseline measurement; thresholds TBD | Performance |
| NFR-API-001 | Build and lint; a run on API 23 | Manual |
| NFR-REL-001 | Release readiness checklist | Manual |

## Environments

| Environment | Availability | Used for | Status |
| --- | --- | --- | --- |
| Honor 8C (BKK-LX2), Android 8.1, API 27, arm64 | Physical device, available | Manual flows, low-end baseline | In use |
| Emulator, API 31 | Image present, flagged by sdkmanager as stored in an unexpected folder | Newer-device checks, blur fallback | NOT RUN |
| Emulator, API 23 | NOT AVAILABLE | NFR-API-001 | Gap, stated in every release |
| Large-screen emulator (600 dp wide or more) | To be set up with the API 31 image | NFR-LAY-001 | NOT RUN |
| CI runner `ubuntu-24.04` | Available | Unit tests, lint, build | In use |

## Conditions to simulate

Offline, slow, failing and empty provider answers, a retired endpoint (HTTP 410), low
storage, largest system text size, TalkBack on, animations removed, and a theme that
differs from the system theme.

## Gates

- Every Must requirement has evidence before a release.
- Lint has 0 errors, and every warning is accepted in writing.
- The museum's terms are re-read before every release.
- A manual smoke run on the Honor 8C is recorded.
- If API 23 is still not verified, the release notes say so.
