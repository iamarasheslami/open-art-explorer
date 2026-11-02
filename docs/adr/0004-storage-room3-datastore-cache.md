# ADR 0004: Room 3 for records, DataStore for settings, bounded caches

- **Status:** Approved with the phase P2 pull request
- **Date:** 2026-11-01

## Context

The app keeps favorites and cached details offline, remembers settings, and caches
images within a bound. Room 3 is the current stable line, generated with KSP.

## Decision

- Store records, favorites and saved-image entries in a Room 3 database.
- Store settings in DataStore.
- Keep image files in a bounded cache directory, separate from saved images in
  app-private storage.
- Favorites are never evicted. Other cached details are removed oldest first when a
  bound is reached. The bounds are decided in the storage slice (OQ-09).
- Versions of Room 3, KSP and DataStore are pinned in the first slice that needs
  them, and CI at compileSdk 36 proves them.

## Alternatives

- Room 2.x: older line, kept only if Room 3 cannot be made to work at our settings.
- Plain files or SharedPreferences for records: less structure and weaker queries.

## Consequences

- The build gains a KSP step, which adds build time.
- The startup theme needs a fast read path, decided in ADR 0006.
