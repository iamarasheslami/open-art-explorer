# Specification

Status: Draft. Requirements are added in groups. A group is approved by the maintainer
in the pull request that adds it. Nothing here proves that a feature exists. Values
marked TBD must be measured or decided, never invented.

## How to read a requirement

Each requirement has an ID, a statement, a priority (Must, Should, Could, Won't), a
rationale, acceptance examples written as Given / When / Then, negative cases and a
verification method. Statements use the form "When <event>, the system shall <result>"
where an event applies.

## Verified provider facts

Source: the documentation of The Metropolitan Museum of Art Collection API
(https://metmuseum.github.io), read on 2026-11-01, and checks run from the
maintainer's network on the same date.

| Fact | Evidence |
| --- | --- |
| Base address `https://collectionapi.metmuseum.org/public/collection`. No API key. Requests are limited to 80 per second. | Documentation |
| The open-access data is released under Creative Commons Zero. Use of the API is subject to the museum's terms and conditions. | Documentation. The terms are not reviewed yet (OQ-01). |
| `/v1/search` is retired. It answered HTTP 410 Gone. | Check from the maintainer's network |
| `/v1.1/search` is paginated with `offset` and `limit`. Default page 100, maximum 500, and `offset` plus `limit` may not exceed 10,000. | Documentation. The endpoint answered from the maintainer's network: 153 results for a keyword query with images, 2,724 for a filter-only query in one department. |
| Search returns a total and object IDs only. Each artwork needs its own request to `/v1/objects/{objectID}`. | Documentation |
| Search filters: department, highlight, on view, has images, medium, geographic location, date range (both ends required), and title, tags or artist-or-culture matching. A search word is not required. | Documentation |
| An object has `isPublicDomain`, `primaryImage`, `primaryImageSmall`, `additionalImages`, `rightsAndReproduction` and `objectURL`. | Documentation. One real record (object 436524) had isPublicDomain true, both image links present and an empty rights line. |

## Group A: provider contract and rights

### DATA-API-001: Documented endpoints only

- **Statement:** The provider adapter shall use only the documented endpoints
  `/v1.1/search`, `/v1/objects/{objectID}` and `/v1/departments`.
- **Priority:** Must
- **Rationale:** The old search endpoint is already gone (HTTP 410). Undocumented
  behavior can disappear without notice. An approved discovery strategy (OQ-02) may
  add another documented endpoint.
- **Acceptance:** Given a search request, when the adapter builds it, then the path is
  `/public/collection/v1.1/search`.
- **Negative cases:** A request to a retired or unknown path is handled as a provider
  error and shown as an error state. It is not retried without limit.
- **Verification:** Contract test of request building; review of the network client.
- **Status:** Draft

### DATA-API-002: Stay under the provider's request limit

- **Statement:** The app shall keep its request rate to the provider below the
  documented limit of 80 requests per second. The app's own concurrency and rate
  budget is TBD until measured.
- **Priority:** Must
- **Rationale:** One gallery page needs one search request plus one request per
  artwork, so uncontrolled loading can exceed the limit quickly.
- **Acceptance:** Given a page that needs several artwork requests, when it loads, then
  requests are issued with bounded concurrency and the rate stays below the limit.
- **Negative cases:** When the provider answers with an error status, the app backs
  off and shows a clear state. It does not retry in a tight loop.
- **Verification:** Unit test of the request limiter with a controllable clock;
  measurement report under NFR-PERF-001.
- **Status:** Draft

### DATA-SRC-001: Provenance on every record

- **Statement:** Every stored or displayed artwork record shall carry the provider
  name, the provider's object ID and, when the provider supplies it, the address of
  the original page.
- **Priority:** Must
- **Rationale:** People must be able to reach the source of every artwork, and
  records from different providers must never be confused.
- **Acceptance:** Given a provider record with an object ID and an `objectURL`, when
  it is mapped, then the app record holds the provider name, that ID and that address.
- **Negative cases:** A missing `objectURL` leaves the address empty and is never
  invented. A record without an object ID is rejected.
- **Verification:** Mapper unit tests, including incomplete records.
- **Status:** Draft

### DATA-RGT-001: Images are shown only when rights are explicit

- **Statement:** The app shall show or download an artwork image only when the
  provider record marks the work as public domain, supplies an image link and has an
  empty `rightsAndReproduction` line. In every other case it shall show the artwork's
  information and a link to the source, without the image.
- **Priority:** Must
- **Rationale:** The dataset is released under CC0, but image availability depends on
  the rights fields. Ambiguous or missing data fails closed. The empty-rights-line
  condition is a proposal (OQ-03).
- **Acceptance:** Given a record with `isPublicDomain` true, an image link and an
  empty rights line, when the gallery shows it, then the thumbnail is displayed.
- **Negative cases:** `isPublicDomain` false; the flag missing or of an unexpected
  type; the flag true but no image link; the flag true but a non-empty rights line.
  In each case no image is shown or downloaded.
- **Verification:** Positive and negative unit tests of the rights policy and mapper.
- **Status:** Draft

## Open questions

| ID | Question | Needed before |
| --- | --- | --- |
| OQ-01 | What do The Met's terms and conditions for API use say about caching images, saving them for the user, attribution and reuse of image links? | Approval of download, cache and viewer requirements |
| OQ-02 | Which discovery strategy is used? No random-artwork endpoint is documented. Options include picking from highlights, from a department's object IDs, or from the full ID list. | Group B |
| OQ-03 | Should a non-empty `rightsAndReproduction` line block the image even when `isPublicDomain` is true? Proposed: yes. | Approval of DATA-RGT-001 |
| OQ-04 | How does the interface tell the user that only the first 10,000 results of a search are reachable? | Group B |

## Approval log

| Group | Status | Date |
| --- | --- | --- |
| A: provider contract and rights | Pending approval | |
