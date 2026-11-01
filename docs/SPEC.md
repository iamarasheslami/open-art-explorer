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

Sources: the documentation of The Metropolitan Museum of Art Collection API
(https://metmuseum.github.io), the museum's Terms and Conditions
(https://www.metmuseum.org/policies/terms-and-conditions, last updated by the museum
on 2018-10-25), both read on 2026-11-01, and checks run from the maintainer's network
on the same date. The terms may be revised at any time, so they are re-read before
every release.

| Fact | Evidence |
| --- | --- |
| Base address `https://collectionapi.metmuseum.org/public/collection`. No API key. Requests are limited to 80 per second. | Documentation |
| `/v1/search` is retired. It answered HTTP 410 Gone. | Check from the maintainer's network |
| `/v1.1/search` is paginated with `offset` and `limit`. Default page 100, maximum 500, and `offset` plus `limit` may not exceed 10,000. | Documentation. The endpoint answered from the maintainer's network: 153 results for a keyword query with images, 2,724 for a filter-only query in one department. |
| Search returns a total and object IDs only. Each artwork needs its own request to `/v1/objects/{objectID}`. | Documentation |
| Search filters: department, highlight, on view, has images, medium, geographic location, date range (both ends required), and title, tags or artist-or-culture matching. A search word is not required. | Documentation |
| An object has `isPublicDomain`, `primaryImage`, `primaryImageSmall`, `additionalImages`, `rightsAndReproduction` and `objectURL`. | Documentation. One real record (object 436524) had isPublicDomain true, both image links present and an empty rights line. |
| Open Access materials (works the museum believes are in the public domain, or whose copyright it waives) may be used for any purpose, commercial or not, free of charge and without permission, under CC0. The API provides images of such works. This includes downloading. | Terms, sections 3 and 7 |
| Materials the museum believes are under copyright are limited to noncommercial, educational, personal or fair use. | Terms, section 3 |
| The museum's trademarks and logos may not be used without written permission. | Terms, section 2 |
| For the API, the museum monitors and stores IP addresses and usage information. It gives no support and no availability guarantee. It may restrict or end the API, limit transactions or require keys in future. It encourages regular refreshing of data because content may change or be removed. | Terms, section 7 |

## Group A: provider contract and rights

### DATA-API-001: Documented endpoints only

- **Statement:** The provider adapter shall use only the documented endpoints
  `/v1.1/search`, `/v1/objects/{objectID}` and `/v1/departments`.
- **Priority:** Must
- **Rationale:** The old search endpoint is already gone (HTTP 410). Undocumented
  behavior can disappear without notice. A later approved requirement may add another
  documented endpoint.
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

### DATA-RGT-001: Images are shown only for public-domain records

- **Statement:** The app shall show or download an artwork image only when the
  provider record marks the work as public domain (`isPublicDomain` is true) and
  supplies an image link. In every other case it shall show the artwork's information
  and a link to the source, without the image.
- **Priority:** Must
- **Rationale:** Open Access works are released under CC0 by the museum's terms, and
  the documented API supplies images for such works. There is no extra local
  condition, so the app follows the provider's own flag (decision on OQ-03). A
  non-empty `rightsAndReproduction` line does not block the image. It is shown with
  the artwork details.
- **Acceptance:** Given a record with `isPublicDomain` true and an image link, when the
  gallery shows it, then the thumbnail is displayed.
- **Negative cases:** `isPublicDomain` false; the flag missing or of an unexpected
  type; the flag true but no image link. In each case no image is shown or downloaded.
- **Verification:** Positive and negative unit tests of the rights policy and mapper.
- **Status:** Draft

### DATA-RGT-002: Rights follow the provider's current data

- **Statement:** When refreshed provider data no longer qualifies a record under
  DATA-RGT-001, the app shall stop showing and offering that record's image, including
  cached copies. A failed refresh shall not be treated as a rights change. The
  refresh interval is TBD (OQ-06).
- **Priority:** Must
- **Rationale:** The museum states that content may change or be removed. The
  maintainer decided that users follow the provider's data, even if that means an
  image is no longer available.
- **Acceptance:** Given a cached record whose image was shown, when a refresh returns
  `isPublicDomain` false, then the image is no longer displayed and its cached copy
  is removed.
- **Negative cases:** When the refresh fails because the device is offline or the
  provider errors, the app keeps the last known state and tries again later.
- **Verification:** Repository tests with a fake provider that changes its answer.
- **Status:** Draft

### NFR-PRIV-002: Disclose the data flow to The Met

- **Statement:** The app's privacy information shall state that artwork data and
  images are requested directly from The Met's servers, which can see technical
  request information such as the device's IP address, under the museum's own
  privacy policy, and shall link to that policy.
- **Priority:** Must
- **Rationale:** The museum's terms say it monitors and stores IP addresses and usage
  information for the API. The app has no server of its own, so every user connects
  to the museum directly.
- **Acceptance:** Given the app's privacy information, when it is read, then the
  statement and the link are present.
- **Negative cases:** A build that adds any other network destination must update the
  statement before release.
- **Verification:** Documentation review in the release checklist; dependency and
  manifest review under NFR-PRIV-001.
- **Status:** Draft

## Decisions on the first open questions (2026-11-01)

| ID | Decision | Effect |
| --- | --- | --- |
| OQ-01 | Resolved by reading the museum's terms. Open Access images may be downloaded and cached. Downloads are a nice-to-have, and the app can ship without them if they become an obstacle. | The download requirement in group C is a Should. Only images that qualify under DATA-RGT-001 are cached or downloaded. |
| OQ-02 | Discovery picks from the museum's highlights first. Other strategies can be added later. | FR-DISC-001 in group B. |
| OQ-03 | No extra condition. `isPublicDomain` true plus an image link is enough, and the app follows the provider's data. | DATA-RGT-001 and DATA-RGT-002. |
| OQ-04 | When a search matches more than 10,000 results, a short, well-designed statement appears at the top of the results. | Search requirement in group B. |

## Open questions

| ID | Question | Needed before |
| --- | --- | --- |
| OQ-05 | If the user saved an image and the provider later stops marking the work as public domain, is the saved file kept or removed? | Group C |
| OQ-06 | How often are cached records refreshed? | Group C |

## Approval log

| Group | Status | Date |
| --- | --- | --- |
| A: provider contract and rights | Revised 2026-11-01, pending approval | |
