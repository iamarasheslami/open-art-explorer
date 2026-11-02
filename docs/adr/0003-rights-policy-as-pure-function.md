# ADR 0003: The rights policy is one pure function

- **Status:** Approved with the phase P2 pull request
- **Date:** 2026-11-01

## Context

Images may be shown, cached or saved only for records that qualify (DATA-RGT-001).
The rule must be impossible to bypass by accident and easy to test.

## Decision

Implement the rule as one pure function in the domain layer. It takes a neutral
record and returns `ImageEligibility`. Only that function creates `Eligible`. Screens,
caches and the saved-images feature accept only `Eligible` values and never inspect
provider flags themselves. A refresh re-runs the function (DATA-RGT-002 and
DATA-RGT-003).

## Alternatives

- Check flags where images are used: simpler at first, but every new use risks a
  missed check.

## Consequences

- The function has a complete positive and negative test table.
- Any change to the rule is a change to one place, and needs a decision record.
