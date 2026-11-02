# Specification

Status: Groups A to D are approved. Requirements are added in groups. A group is approved by the maintainer
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
- **Status:** Approved (2026-11-01)

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
- **Status:** Approved (2026-11-01)

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
- **Status:** Approved (2026-11-01)

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
- **Status:** Approved (2026-11-01)

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
- **Status:** Approved (2026-11-01)

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
- **Status:** Approved (2026-11-01)

## Group B: experience and browsing

### FR-START-001: Startup that never looks stuck

- **Statement:** When the app starts, the system shall show the app background in the
  user's chosen theme with the logo at the center. When the home screen has its first
  state ready (content, cached content, or an empty or error state), the logo and the
  background shall change into the home screen with an animated transition. The start
  screen shall not stay longer than a ceiling (TBD, OQ-08) when the network is slow.
- **Priority:** Must
- **Rationale:** The app is online-first. The start must feel responsive whatever the
  network speed, with no visible jump between backgrounds.
- **Acceptance:** Given a cold start on a slow network, when the app launches, then the
  logo on the app background is shown, no blank or differently colored frame appears,
  and the transition starts when the first state is ready or the ceiling is reached.
- **Negative cases:** The chosen theme differs from the system theme (no flash of the
  wrong background); the device is offline; reduced-motion setting is on (fade only).
- **Verification:** Screen recordings on the Honor 8C (API 27) and on a device or
  emulator with API 31 or newer; unit test of the readiness condition.
- **Status:** Approved with the phase P2 pull request

### FR-NAV-001: Four top-level destinations

- **Statement:** The app shall offer four top-level destinations: Discover, Search,
  Favorites and Settings. Each keeps its own state while the user moves between them.
- **Priority:** Must
- **Rationale:** Matches the approved navigation (Q1). Wide windows use a rail
  (NFR-LAY-001).
- **Acceptance:** Given the home screen, when the user taps Search and then Discover,
  then Discover shows the same content and scroll position as before.
- **Negative cases:** System back from a top-level destination other than Discover
  returns to Discover first, then leaves the app.
- **Verification:** UI tests of navigation state; manual back-gesture check.
- **Status:** Approved with the phase P2 pull request

### FR-GAL-001: Image-first gallery

- **Statement:** The gallery shall show artworks as a grid of cards, two columns on
  phone-width windows and more on wider ones, in pages of 20 artworks (initial value,
  tuned after measurement). Each card shows the thumbnail when DATA-RGT-001 allows it,
  otherwise a placeholder with the title. More artworks load when the user reaches
  the end of the list.
- **Priority:** Must
- **Rationale:** One search request returns only IDs, so each page needs one request
  per artwork (DATA-API-002).
- **Acceptance:** Given a page of results, when it loads, then each card shows a
  thumbnail or a placeholder and no record appears that the provider did not return.
- **Negative cases:** A record fails to load (only that card shows an error state with
  retry); the list ends (an end-of-results note); the user scrolls fast (requests are
  cancelled for cards that left the screen).
- **Verification:** Mapper and repository tests with fixtures; UI test of states.
- **Status:** Approved with the phase P2 pull request

### FR-DISC-001: Discover from highlights

- **Statement:** Discover shall show artworks picked at random from the museum's
  highlights that have images. A new random selection is made each time the app
  starts and when the user pulls down to refresh. There is no shuffle button.
- **Priority:** Must
- **Rationale:** Decision on OQ-02. The strategy is documented in the architecture
  notes. Other strategies can be added later.
- **Acceptance:** Given reachable highlights, when Discover opens, then it shows 20
  artworks from the highlight set in random order.
- **Negative cases:** The provider is unreachable and nothing is cached (error state
  with retry); cached selection exists (shown with an offline note); fewer than 20
  highlights are returned (shows what exists).
- **Verification:** Strategy unit test with a seeded random source; smoke test.
- **Status:** Approved with the phase P2 pull request

### FR-SEA-001: Keyword search with paging

- **Statement:** When the user submits a keyword, the app shall query the provider's
  `/v1.1/search` endpoint and show the results page by page with `offset` and `limit`.
- **Priority:** Must
- **Rationale:** The only supported search endpoint (DATA-API-001).
- **Acceptance:** Given the keyword "sunflowers", when it is submitted, then the first
  page appears and the next page loads when the user reaches the end of the list.
- **Negative cases:** Empty keyword with no filter (prompt, no request); no match (an
  empty state); network error (error state with retry).
- **Verification:** Contract test of request building; repository paging test.
- **Status:** Approved with the phase P2 pull request

### FR-SEA-002: Honest note when results exceed the reachable limit

- **Statement:** When a search matches more than 10,000 results, the app shall show a
  short, well-designed statement at the top of the results saying that only the first
  10,000 can be browsed. The statement is not shown otherwise, and requests stop at
  the limit.
- **Priority:** Must
- **Rationale:** Decision on OQ-04. The provider allows `offset` plus `limit` of at
  most 10,000.
- **Acceptance:** Given a search with a total of 12,345, when results appear, then the
  statement is shown at the top. Given a total of 153, then it is not shown.
- **Negative cases:** The user reaches the 10,000th result (no further request; an
  end-of-results note); the statement is announced by TalkBack.
- **Verification:** Repository test with fixture totals; UI test; accessibility check.
- **Status:** Approved with the phase P2 pull request

### FR-FLT-001: Only verified filters

- **Statement:** The app shall offer these filters: department, highlights only, has
  images (on by default) and date range. Filters the provider does not support shall
  not appear as working controls.
- **Priority:** Must
- **Rationale:** Decision on Q5. The departments list comes from `/v1/departments`.
- **Acceptance:** Given the department list, when the user selects a department, then
  the search request carries that department.
- **Negative cases:** Only one end of the date range is given (inline message, no
  request, because the provider needs both); begin later than end (inline message).
- **Verification:** Capability and contract tests; UI test of validation.
- **Status:** Approved with the phase P2 pull request

### FR-DET-001: Artwork details

- **Statement:** The detail screen shall show title, artist, date, culture or period,
  medium, dimensions, department, credit line, the rights line when present, and a
  link to the museum's page. Fields the provider leaves empty are omitted, never
  replaced by invented text.
- **Priority:** Must
- **Rationale:** Decision on Q6, with provenance from DATA-SRC-001.
- **Acceptance:** Given a full record, when the detail screen opens, then every listed
  field is shown and the source link opens the museum page.
- **Negative cases:** Missing artist or dimensions (rows omitted); no image (details
  and link still shown); the original page cannot be opened (a clear message).
- **Verification:** Mapper and UI state tests.
- **Status:** Approved with the phase P2 pull request

### FR-IMG-001: Large image viewer

- **Statement:** When an artwork qualifies under DATA-RGT-001, the user shall be able
  to open its large image with pinch zoom and double-tap zoom. The thumbnail stays
  visible while the large image loads. Additional images are a later option.
- **Priority:** Must
- **Rationale:** Decision on Q7.
- **Acceptance:** Given an eligible artwork, when the user opens the viewer and
  double-taps, then the image zooms in and a second double-tap zooms out.
- **Negative cases:** The large image fails to load (error with retry, thumbnail
  kept); no eligible image (no viewer action); data saver is on (the small image is
  used until the user asks for the high-quality one).
- **Verification:** UI tests; manual gesture check on the Honor 8C.
- **Status:** Approved with the phase P2 pull request

## Group C: favorites, offline and saved images

### FR-FAV-001: Local favorites

- **Statement:** The user shall be able to add and remove a favorite from a card and
  from the detail screen. Favorites persist across restarts without an account or a
  network and are listed newest first.
- **Priority:** Must
- **Rationale:** Decision on Q8. Folders and tags are not part of v1.
- **Acceptance:** Given a favorited artwork, when the app is closed and reopened, then
  the artwork is still in Favorites.
- **Negative cases:** Removing a favorite that was already removed (no error); storage
  full (a clear message, no data loss).
- **Verification:** Database and repository tests; UI test.
- **Status:** Approved with the phase P2 pull request

### FR-OFF-001: Defined offline behavior

- **Statement:** Favorites shall keep their details available offline. Details of
  artworks the user opened are kept in a bounded cache, oldest removed first (size
  TBD, OQ-09). Images use a bounded cache and may be absent offline, in which case a
  placeholder and an honest note are shown. Records that were never cached show a
  clear network-dependent state.
- **Priority:** Must
- **Rationale:** Decision on Q9. The app promises only what it stores.
- **Acceptance:** Given a favorite and no network, when the user opens it, then its
  details are shown.
- **Negative cases:** An image missing from the cache offline (placeholder); the
  cache reaches its bound (oldest entries removed, favorites never removed).
- **Verification:** Repository and UI tests in a simulated offline state.
- **Status:** Approved with the phase P2 pull request

### DATA-REF-001: Refresh on open, weekly

- **Statement:** When a cached record is opened, is older than 7 days and the network
  is available, the app shall refresh it from the provider. There is no background
  refresh. A failed refresh keeps the last known state.
- **Priority:** Must
- **Rationale:** Decision on OQ-06. The museum updates its open data weekly. This
  makes DATA-RGT-002 effective without background work or extra permissions.
- **Acceptance:** Given a record cached 8 days ago, when it is opened online, then it
  is refreshed and its timestamp is updated.
- **Negative cases:** Offline (no refresh, no error shown); provider error (last state
  kept, retry on the next open).
- **Verification:** Repository tests with a controllable clock.
- **Status:** Approved with the phase P2 pull request

### FR-DL-001: Save an image

- **Statement:** The user shall be able to save the full image of a qualifying artwork
  into app-private storage, see a Saved list, delete saved images and share one
  through the system share sheet. No storage permission is needed. Saved images are
  separate from the temporary cache and are not removed by clearing the cache.
- **Priority:** Should
- **Rationale:** Decision on OQ-01 and Q10. The museum's terms allow downloading Open
  Access images. The feature may ship later if it becomes an obstacle.
- **Acceptance:** Given an eligible artwork, when the user taps Save, then the image
  appears in the Saved list and can be opened offline.
- **Negative cases:** The artwork is not eligible (no Save action); storage is low (a
  clear message, nothing partial is kept); the download fails (no partial file).
- **Verification:** File lifecycle and storage tests; manual check of sharing.
- **Status:** Approved with the phase P2 pull request

### DATA-RGT-003: Saved images follow the provider

- **Statement:** When a refresh shows that a saved image no longer qualifies under
  DATA-RGT-001, the app shall remove the saved file and tell the user which saved
  image was removed.
- **Priority:** Must
- **Rationale:** Decision on OQ-05, consistent with DATA-RGT-002.
- **Acceptance:** Given a saved image whose refreshed record has `isPublicDomain`
  false, when the refresh completes, then the file is deleted and a notice is shown.
- **Negative cases:** The refresh fails (the saved file stays until a successful
  refresh).
- **Verification:** Repository and file tests with a fake provider.
- **Status:** Approved with the phase P2 pull request

## Group D: look, settings and quality attributes

### FR-THE-001: Theme choice

- **Statement:** Settings shall offer System default (initial), Light and Dark. The
  choice persists, applies without restarting, and also decides the startup
  background (FR-START-001).
- **Priority:** Must
- **Rationale:** Decision on Q13.
- **Acceptance:** Given the choice Dark while the system is Light, when the app
  starts, then the start screen and the home screen are both dark with no light flash.
- **Negative cases:** The preference cannot be read at startup (falls back to the
  system theme).
- **Verification:** Preference and UI tests; screen recording on the Honor 8C.
- **Status:** Approved with the phase P2 pull request

### FR-UIX-001: Glass design with a graceful fallback

- **Statement:** Surfaces shall use a glassmorphism design: real background blur on
  devices that support it (Android 12 and newer), and an equivalent layered,
  translucent look on older devices that does not rely on real blur. A Reduced
  effects mode uses solid surfaces. Text contrast meets WCAG AA in every mode.
- **Priority:** Must
- **Rationale:** Decisions on Q15 and Q14. Real blur is available only on newer
  devices, so older ones need a designed alternative.
- **Acceptance:** Given Full effects on an API 31 or newer device, when a glass surface
  is over an image, then the image behind it is blurred. Given the Honor 8C, then the
  surface is translucent and layered without blur.
- **Negative cases:** Reduced effects or data saver (solid surfaces); high contrast
  setting (stronger borders and opaque text backgrounds).
- **Verification:** Screenshots on both device classes; contrast checks; performance
  measurement on the Honor 8C (OQ-07).
- **Status:** Approved with the phase P2 pull request

### FR-ADA-001: High quality by default, Data saver on request

- **Statement:** Settings shall offer Image quality with High quality as the default
  and a Data saver option. Choosing Data saver shall reduce data use everywhere: small
  images instead of large ones until the user asks for high quality, no prefetching,
  Reduced visual effects and simpler motion. Choosing High quality restores the
  defaults.
- **Priority:** Must
- **Rationale:** Decision on Q14.
- **Acceptance:** Given Data saver on, when the gallery and the viewer open, then only
  small images are requested unless the user taps the high-quality action.
- **Negative cases:** Changing the setting while images load (in-flight requests end
  cleanly); the setting is read at startup before the first request.
- **Verification:** Preference logic tests; request-count test with a fake provider.
- **Status:** Approved with the phase P2 pull request

### FR-SET-001: Rich, organized settings

- **Statement:** Settings shall be organized in groups. Must: theme, image quality,
  clear caches, About. Should: visual effects, motion (follow system or reduced),
  contrast (standard or high), grid density (compact, comfortable, spacious), show
  titles on cards, Wi-Fi-only image loading, cache size limit, saved images
  management, reset to defaults. Could: wallpaper colors on Android 12 and newer, an
  in-app text scale on top of the system scale, export of the favorites list.
  About shall show the version, the source code link, the license, the open-source
  libraries with their licenses, the museum attribution and the privacy statement.
- **Priority:** Must for the first group, Should and Could as listed
- **Rationale:** Decision on Q16. The groups follow common Material 3 settings
  patterns and the system settings the app respects.
- **Acceptance:** Given any setting, when the user changes it and restarts the app,
  then the choice is still applied.
- **Negative cases:** A stored value that is no longer valid (the default is used).
- **Verification:** Preference and UI tests; manual review of the About screen.
- **Status:** Approved with the phase P2 pull request

### FR-BRD-001: Original launcher icon

- **Statement:** The app shall have an original adaptive launcher icon with a
  monochrome layer for themed icons, legible at small sizes, and no museum marks. The
  start screen uses the same logo.
- **Priority:** Must before release
- **Rationale:** Decision on Q18. The museum's trademarks may not be used (terms).
- **Acceptance:** Given a launcher with themed icons, when the app is installed, then
  the icon follows the system color and stays recognizable.
- **Negative cases:** Launchers that crop the icon in different shapes (the logo stays
  inside the safe area).
- **Verification:** Visual review in several shapes; lint no longer reports a missing
  icon.
- **Status:** Approved with the phase P2 pull request

### NFR-LAY-001: Edge-to-edge, predictive back and adaptive layouts

- **Statement:** The app shall support edge-to-edge display, the predictive back
  gesture and adaptive layouts, because it targets Android 16 (API 36). On windows
  600 dp wide or more, the bottom bar is replaced by a navigation rail and the
  gallery uses more columns. No screen orientation lock is used.
- **Priority:** Must
- **Rationale:** For apps targeting API 36, edge-to-edge cannot be opted out of,
  predictive back is on by default on Android 16, and orientation and resizability
  restrictions are ignored on large screens.
- **Acceptance:** Given a window 840 dp wide, when the app opens, then a rail is shown
  and the gallery uses more than two columns.
- **Negative cases:** System bars overlapping content (padding handled); back gesture
  on a top-level destination (follows FR-NAV-001).
- **Verification:** UI tests with window size variations; manual gesture check.
- **Status:** Approved with the phase P2 pull request

### NFR-ACC-001: Accessibility

- **Statement:** Core screens shall work with TalkBack, support the system's largest
  text size without clipped content, keep touch targets at least 48 dp, meet WCAG AA
  contrast, never use color alone to carry meaning, and respect the system setting
  that removes animations.
- **Priority:** Must
- **Rationale:** Approved decision D-014 (accessibility in v1).
- **Acceptance:** Given TalkBack on, when the user moves through Discover, details and
  Settings, then every control is announced with a name and a role.
- **Negative cases:** Largest text size (no truncated essential information).
- **Verification:** Accessibility checklist, automated checks and manual TalkBack
  review.
- **Status:** Approved with the phase P2 pull request

### NFR-PRIV-001: Privacy by design

- **Statement:** The app shall contain no ads, accounts, analytics, tracking, third-
  party crash-reporting service or required paid service. Its permissions are limited
  to network access and network state.
- **Priority:** Must
- **Rationale:** Approved decision D-016.
- **Acceptance:** Given a release build, when the manifest and the dependency list are
  reviewed, then no other permission or service is present.
- **Negative cases:** A new dependency that brings a tracking component (rejected in
  review).
- **Verification:** Manifest and dependency review in CI and in the release checklist.
- **Status:** Approved with the phase P2 pull request

### NFR-SEC-001: Trust only expected addresses

- **Statement:** The app shall load images and open links only when the address uses
  https and belongs to a provider host from a verified list (TBD, confirmed from real
  records). Other addresses are ignored.
- **Priority:** Must
- **Rationale:** Provider data is external input and must not redirect the app.
- **Acceptance:** Given a record whose image address uses http or an unexpected host,
  when it is mapped, then no image is requested.
- **Negative cases:** Malformed address (ignored, no crash).
- **Verification:** Mapper unit tests.
- **Status:** Approved with the phase P2 pull request

### NFR-RES-001: No crash on common failures

- **Statement:** The app shall not crash on provider errors, missing fields or images,
  empty results, timeouts, being offline or low storage. Each case shows a clear,
  recoverable state.
- **Priority:** Must
- **Rationale:** Online-first apps meet these conditions daily.
- **Acceptance:** Given a malformed provider response, when the gallery loads, then an
  error state with retry is shown.
- **Negative cases:** The provider restricts or ends the API (the cached content stays
  usable and the error is honest).
- **Verification:** Unit, integration and UI negative tests.
- **Status:** Approved with the phase P2 pull request

### NFR-MOD-001: Provider-neutral core

- **Statement:** Screens and domain logic shall use provider-neutral models and
  contracts, never the museum's raw data classes, so a second provider can be added
  behind the adapter.
- **Priority:** Must
- **Rationale:** Approved decision D-008 and the adapter design.
- **Acceptance:** Given the code base, when dependencies are reviewed, then no screen
  or domain package depends on the museum adapter package.
- **Negative cases:** A provider-specific field leaking into a screen (rejected in
  review).
- **Verification:** Architecture review and a dependency check test.
- **Status:** Approved with the phase P2 pull request

### NFR-PERF-001: Measured performance

- **Statement:** The gallery shall use lazy, paged loading, appropriately sized
  thumbnails, cancellation and bounded caches. Numeric budgets are TBD until a
  baseline is measured on the Honor 8C and an emulator.
- **Priority:** Must
- **Rationale:** Thresholds must come from measurement, not guesses.
- **Acceptance:** Given a measured baseline, when budgets are approved, then they are
  recorded here with the device and method.
- **Negative cases:** A change that makes the gallery slower than the budget (blocked
  in review).
- **Verification:** Profiling report; thresholds TBD.
- **Status:** Approved with the phase P2 pull request

### NFR-API-001: Minimum Android version

- **Statement:** The app shall build and run on Android 6.0 (API 23) and newer.
- **Priority:** Must
- **Rationale:** Approved decision D-003.
- **Acceptance:** Given an API 23 device or emulator, when the app is installed, then
  it starts and the main flows work.
- **Negative cases:** A library or API call that needs a newer level (found by the
  build or by testing).
- **Verification:** Build, lint and an API 23 run. NOT VERIFIED at API 23 until an
  image or device is available; the only device so far runs API 27.
- **Status:** Approved with the phase P2 pull request

### NFR-REL-001: Release integrity

- **Statement:** A release shall have a reproducible build path, versioned notes, the
  correct source license and rights notices, a re-read of the museum's terms, an
  artifact checksum, and a documented way to withdraw it.
- **Priority:** Must before release
- **Rationale:** Rights or serious defects may require withdrawal.
- **Acceptance:** Given a release candidate, when the checklist is completed, then
  every item has evidence.
- **Negative cases:** A missing item blocks the release.
- **Verification:** Release readiness checklist.
- **Status:** Approved with the phase P2 pull request

## Decisions on the first open questions (2026-11-01)

| ID | Decision | Effect |
| --- | --- | --- |
| OQ-01 | Resolved by reading the museum's terms. Open Access images may be downloaded and cached. Downloads are a nice-to-have, and the app can ship without them if they become an obstacle. | FR-DL-001 is a Should. Only images that qualify under DATA-RGT-001 are cached or downloaded. |
| OQ-02 | Discovery picks from the museum's highlights first. Other strategies can be added later. | FR-DISC-001 |
| OQ-03 | No extra condition. `isPublicDomain` true plus an image link is enough, and the app follows the provider's data. | DATA-RGT-001 and DATA-RGT-002 |
| OQ-04 | When a search matches more than 10,000 results, a short, well-designed statement appears at the top of the results. | FR-SEA-002 |
| OQ-05 | A saved image is removed when the provider stops marking the work as public domain. | DATA-RGT-003 |
| OQ-06 | Cached records are refreshed when opened and older than 7 days. No background work. | DATA-REF-001 |

## Decisions on the product choices (2026-11-01)

| ID | Decision | Requirement |
| --- | --- | --- |
| Q1 | Bottom bar with Discover, Search, Favorites and Settings | FR-NAV-001 |
| Q2, Q3 | Two-column grid on phones, more columns on wider windows, 20 artworks per page as an initial value | FR-GAL-001 |
| Q4 | Discover picks random highlights at start and on pull-to-refresh, with no shuffle button | FR-DISC-001 |
| Q5 | Filters: department, highlights only, has images (on by default), date range | FR-FLT-001 |
| Q6, Q7 | Detail fields as listed. Viewer with pinch and double-tap zoom | FR-DET-001, FR-IMG-001 |
| Q8 to Q10 | Favorites newest first. Offline as defined. Save image to app-private storage | FR-FAV-001, FR-OFF-001, FR-DL-001 |
| Q13 | Theme: System default (initial), Light, Dark | FR-THE-001 |
| Q14 | High quality by default, with a Data saver option that changes the whole app | FR-ADA-001 |
| Q15 | Full glassmorphism where blur is supported, an equivalent layered look on older devices | FR-UIX-001 |
| Q16 | Rich settings in groups, following current Material 3 patterns | FR-SET-001 |
| Q17 | One module, MVVM, hand-written wiring, Room 3, DataStore, OkHttp, kotlinx.serialization, Coil 3, Navigation 3 | Architecture notes |
| Q18 | Original adaptive launcher icon with a monochrome layer | FR-BRD-001 |
| Extra | The start screen shows the app background and logo and flows into the home screen | FR-START-001 |

## Open questions

| ID | Question | Needed before |
| --- | --- | --- |
| OQ-07 | Should low-end devices default to Reduced visual effects automatically? Decide after measuring on the Honor 8C. | Design phase |
| OQ-08 | What is the longest the start screen may stay (the ceiling), and which technique keeps the startup background in the chosen theme? A short test decides both. Initial proposal for the ceiling: 3 seconds. | FR-START-001 implementation |
| OQ-09 | What are the cache size limits and the choices offered in Settings? | Storage implementation |

## Approval log

| Group | Status | Date |
| --- | --- | --- |
| A: provider contract and rights | Approved | 2026-11-01 |
| B: experience and browsing | Approved with the phase P2 pull request | |
| C: favorites, offline and saved images | Approved with the phase P2 pull request | |
| D: look, settings and quality attributes | Approved with the phase P2 pull request | |
