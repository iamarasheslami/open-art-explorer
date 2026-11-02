# ADR 0001: One module, MVVM, hand-written wiring

- **Status:** Approved with the phase P2 pull request
- **Date:** 2026-11-01

## Context

The app is small, built by one maintainer, and must stay easy to understand. Build
time matters on a two-core laptop.

## Decision

Use one Gradle module (`:app`) with the packages in `docs/ARCHITECTURE.md`. Use MVVM
with one-way data flow: ViewModels expose a `StateFlow` of a single `UiState` and
accept events as function calls. Wire dependencies by hand in one place (an
application container), with no dependency-injection framework.

## Alternatives

- Several Gradle modules: better isolation, slower builds and more files to manage.
  Revisit if a package grows large or a second app needs the core.
- Hilt or Koin: less manual wiring, more build and learning cost for a small graph.

## Consequences

- Package rules are enforced by a test instead of by module boundaries.
- Replacing the wiring with a framework later stays possible because dependencies are
  already passed through constructors.
