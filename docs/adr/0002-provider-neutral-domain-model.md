# ADR 0002: Provider-neutral domain model behind an adapter

- **Status:** Approved with the phase P2 pull request
- **Date:** 2026-11-01

## Context

The first provider is The Metropolitan Museum of Art, but the project plans to add
others. The museum's records contain many fields, some of them inconsistent.

## Decision

Screens and domain code use provider-neutral models. Each provider has an adapter
that owns the provider's address rules, data classes and mapper. The adapter turns
every record into a neutral model and every failure into a `ProviderError`. Records
carry the provider name and the provider's object ID (DATA-SRC-001).

## Alternatives

- Use the museum's data classes everywhere: faster at first, but every later provider
  would force changes in screens.

## Consequences

- A mapper layer must be written and tested with real fixtures.
- Adding a provider means adding one adapter and its fixtures.
