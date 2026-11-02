# ADR 0006: Compose, Navigation 3, glass tokens and a splash-based start

- **Status:** Approved with the phase P2 pull request
- **Date:** 2026-11-01

## Context

The interface must follow Android 16 rules (edge-to-edge, predictive back, adaptive
layouts), offer a glass-style design that works on older devices, and start without
looking stuck on a slow network.

## Decision

- Build the UI with Jetpack Compose and Material 3, with custom glass tokens
  (`docs/DESIGN.md`).
- Use Navigation 3 for navigation, with a bottom bar on narrow windows and a rail on
  wide ones.
- Use the core-splashscreen library for the start. The start screen stays until the
  home screen's first state is ready or a ceiling is reached, then a custom exit
  animation flows into the home screen.
- The theme and image quality are read before the first frame by a small fast path.
  The exact technique and the ceiling value are decided by a short test (OQ-08).
- Real backdrop blur is used only where the platform supports it. The technique
  (a maintained library or a custom layer) is chosen by a short test in the design
  phase (OQ-07).

## Alternatives

- Navigation Compose 2.9: stable and well known, and the fallback if Navigation 3
  cannot be built at compileSdk 36.
- A custom splash view: more control, but it fights the system splash.

## Consequences

- Two device classes must be checked for startup and glass behavior.
- The start flow is a risk until the test is done.
