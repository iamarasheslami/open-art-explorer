# ADR 0005: OkHttp, kotlinx.serialization and Coil 3

- **Status:** Approved with the phase P2 pull request
- **Date:** 2026-11-01

## Context

The app makes many small requests, parses JSON from an external source, and shows many
images. It must stay under the provider's request limit and tolerate bad data.

## Decision

- Use OkHttp as the HTTP client, with a limiter that bounds concurrency and request
  rate for all provider calls.
- Use kotlinx.serialization for JSON, with tolerant settings for unknown fields and
  explicit handling of missing or wrongly typed values.
- Use Coil 3 for images, with its OkHttp network module, sized requests, a bounded
  disk cache and placeholders.
- Only https addresses on verified hosts are requested (NFR-SEC-001).
- Versions are pinned when first used and proven by CI.

## Alternatives

- Ktor client, Retrofit or Moshi: workable, but they add concepts without a need here.
- Glide: mature, with less Compose-native support.

## Consequences

- Tests use a local fake server for the adapter.
- The limiter and its measurements are part of the performance report.
