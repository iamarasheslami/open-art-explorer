# Architecture

Status: Approved with the phase P2 pull request. This describes the design the code
will follow. No feature code exists yet.

## Context

Open Art Explorer is an Android app with no server of its own. It reads artwork data
and images directly from a museum provider and keeps a bounded local copy. The first
provider is The Metropolitan Museum of Art. Everything provider-specific sits behind an
adapter, so a second provider can be added without changing screens.

## Layers and data flow

```
Compose screens
      |  user events and UiState (StateFlow)
ViewModels  (MVVM, one-way data flow)
      |
Domain: models, rights policy, repository contracts
      |
Data: repository implementations, Room 3 database, DataStore settings,
      image and file storage, refresh policy
      |
Provider contract (ArtProvider)  <--  Met adapter: OkHttp client, DTOs, mapper
```

## Packages

One Gradle module (`:app`) with these packages under
`io.github.iamarasheslami.openartexplorer`:

| Package | Contents | May depend on |
| --- | --- | --- |
| `ui` | Screens, components, theme, navigation, ViewModels | `domain` |
| `domain` | Provider-neutral models, rights policy, repository and provider contracts | nothing else in the app |
| `data` | Repository implementations, database, settings, caches, saved images | `domain` |
| `provider.met` | The Met adapter: client, DTOs, mapper | `domain` |
| `startup` | Start screen, readiness condition, exit animation | `ui`, `domain` |
| `core` | Dispatchers, clock, random source, result and error types | nothing |

The rule `ui` never depends on `provider.met` is checked by a test (NFR-MOD-001).

## Core types (names, not final code)

- `Artwork` and `ArtworkSummary`: provider-neutral records that carry provider name,
  object ID and source address (DATA-SRC-001).
- `ImageEligibility`: either `Eligible` with the thumbnail and large image addresses,
  or `NotEligible` with a reason. Only the rights policy creates it (ADR 0003).
- `SearchQuery`, `SearchPage` (IDs, total, whether the limit was reached), `Department`.
- `ProviderError`: a closed set of failures such as no network, timeout, not found,
  retired endpoint, server error and unreadable answer.
- `UserSettings`: theme, image quality, visual effects, motion, contrast, grid density
  and the other values in FR-SET-001.

## Main flows

1. **Discover:** fetch highlight IDs that have images, pick 20 at random with an
   injected random source, load each record, map it, apply the rights policy, emit
   state. Cache the ID list.
2. **Search:** one `/v1.1/search` call per page of IDs, then one record request per
   ID with bounded concurrency. Stop at the 10,000 limit and expose the flag for the
   note (FR-SEA-002).
3. **Details and refresh:** show the cached record at once. When it is older than 7
   days and the network is available, refresh it and re-apply the rights policy
   (DATA-REF-001, DATA-RGT-002).
4. **Favorites:** a table of record IDs with timestamps. Their details are kept
   offline and are never evicted.
5. **Saved images:** download an eligible image to app-private storage, record it, and
   delete it if a refresh makes the record ineligible (DATA-RGT-003).

## Cross-cutting rules

- **Request limiting:** all provider calls go through one limiter that bounds
  concurrency and stays below the documented 80 requests per second (DATA-API-002).
- **Cancellation:** requests for items that left the screen are cancelled.
- **Errors:** every failure becomes a `ProviderError` and then a recoverable UI state.
  Nothing throws to the screen.
- **Addresses:** only https addresses on verified provider hosts are used
  (NFR-SEC-001).
- **Settings at startup:** the theme and image quality are available before the first
  frame, so the start screen matches the chosen theme (FR-START-001, ADR 0006).
- **Testing seams:** clock, random source, dispatchers and the provider are injected.

## Decisions

The reasoning is in `docs/adr/`: 0001 single module and MVVM, 0002 provider-neutral
model, 0003 rights policy, 0004 storage and cache, 0005 network and images, 0006 UI,
navigation and startup.

## Open items

- The first storage slice proves the Room 3 test setup and the exact KSP version.
- The backdrop blur technique is decided by a short test in the design phase (OQ-07
  and `docs/DESIGN.md`).
- Library versions are pinned when first added and proven by CI at compileSdk 36.
