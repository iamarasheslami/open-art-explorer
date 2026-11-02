# Design notes

Status: Approved with the phase P2 pull request. Values marked proposal are starting
points, tuned on real devices in the design phase. Nothing here is built yet.

## Direction: Gallery Glass

An image-first interface where the artwork is the star. Content sits on a calm deep
background. Controls and information float on glass-style surfaces. The same look is
kept on every device: real backdrop blur where the platform supports it, and a designed
layered look where it does not.

## Navigation map

| Destination | Route | Opens |
| --- | --- | --- |
| Discover | `discover` | Details |
| Search | `search` | Filters sheet, Details |
| Favorites | `favorites` | Details |
| Settings | `settings` | Appearance, Images and data, Saved images, Accessibility, About |
| Details | `details/{provider}/{id}` | Image viewer, source link, Save |
| Image viewer | `viewer/{provider}/{id}` | Back to Details |

Narrow windows (under 600 dp) use a bottom bar. Wider windows use a navigation rail
(NFR-LAY-001). System back and the predictive back gesture follow FR-NAV-001.

## Startup flow (FR-START-001)

1. The system start screen shows the background token `bg/base` of the chosen theme
   with the logo in the center. The start theme and the home screen use the same
   token, so there is no jump between backgrounds.
2. The start screen is held until the home screen reports its first state (content,
   cached content, or an empty or error state), or until the ceiling is reached
   (OQ-08, initial proposal 3 seconds).
3. A custom exit animation flows into the home screen: the logo scales up slightly and
   fades while the background cross-fades into the home background. Proposal: 500 ms.
   With reduced motion the transition is a 150 ms fade.
4. The animated icon is available on Android 12 and newer only. Older devices use a
   still logo and the same custom exit animation.
5. The chosen theme is applied before the first frame. A short test decides the
   technique (OQ-08).

## Color tokens (proposal)

| Token | Dark | Light |
| --- | --- | --- |
| `bg/base` | #0B1020 | #F4F6FF |
| `surface/solid` | #1A2140 | #FFFFFF |
| `text/primary` | #F2F4FF | #12172B |
| `text/secondary` | #B8BFDD | #4A5170 |
| `accent/gold` | #F2C14E | #8A5A00 (text) |
| `accent/teal` | #3DD6C3 | not used for text |
| `accent/indigo` | not used | #3A4BD8 |

Contrast ratios computed for these solid pairs (WCAG AA needs 4.5 for normal text):

| Pair | Ratio |
| --- | --- |
| Dark: primary text on `bg/base` | 17.27 |
| Dark: secondary text on `bg/base` | 10.40 |
| Dark: gold on `bg/base` | 11.28 |
| Dark: teal on `bg/base` | 10.46 |
| Dark: primary text on `surface/solid` | 14.35 |
| Dark: secondary text on `surface/solid` | 8.64 |
| Light: primary text on `bg/base` | 16.46 |
| Light: secondary text on `bg/base` | 7.21 |
| Light: indigo on `bg/base` | 6.13 |
| Light: deep gold on `bg/base` | 5.50 |
| Light: primary text on `surface/solid` | 17.75 |
| Light: secondary text on `surface/solid` | 7.77 |

Text over glass surfaces on top of images is not covered by this table. It is measured
on real artworks in the design phase (FR-UIX-001).

## Glass surfaces (proposal)

| Mode | Surface | Notes |
| --- | --- | --- |
| Full effects, Android 12 and newer | Blurred backdrop (about 24 dp radius), tint at about 55 percent (dark) or 60 percent (light), 1 dp light border at about 18 percent (dark) or 70 percent (light) | The blur technique is chosen by a test (OQ-07) |
| Full effects, older Android | Layered translucent tint, soft top-to-bottom gradient, 1 dp border highlight, soft shadow, no real blur | Designed to give the same feeling |
| Reduced effects or Data saver | Solid `surface/solid` with the same border | No blur, no heavy shadows |
| High contrast | Opaque text backgrounds, stronger borders | Overrides the other modes |

## Shape, spacing, type and motion (proposal)

- Spacing follows a 4 dp grid. Screen padding is 16 dp on narrow windows and 24 dp on
  wider ones.
- Card corner radius 20 dp. Touch targets are at least 48 dp.
- Type uses the Material 3 scale with the system font. Text follows the system font
  size. Layouts must survive the largest setting.
- Motion durations: short 150 ms, medium 300 ms, long 500 ms, with a decelerating
  curve. All motion shortens or becomes a fade when reduced motion is on.

## Components

Artwork card, glass top bar, bottom bar and rail, filter sheet, skeleton loader, empty
state, error state with retry, results note for more than 10,000 matches, detail rows,
zoomable viewer, settings rows (switch, choice, slider), saved-images list.

## Logo and icon brief: The Open Frame

A rounded-square picture frame with a gap in its top-right corner. A four-point spark,
like a compass star, rises through the gap. The frame is a warm gold gradient, the
spark is teal, and the background is a deep indigo gradient. The meaning is "open" and
"explore". It uses no museum marks.

- Adaptive icon: background layer, foreground layer inside the platform safe area, and
  a single-color monochrome layer for themed icons.
- The start screen uses the same logo.
- Final artwork is drawn as vector graphics in the design phase.

## Accessibility rules for every component

Name and role for TalkBack, touch targets of 48 dp or more, WCAG AA contrast, no
meaning by color alone, visible focus, respect for the system setting that removes
animations, and clear labels instead of icon-only controls where meaning is not
obvious.

## Open items

- OQ-07: choose the backdrop blur technique and whether low-end devices default to
  Reduced effects.
- OQ-08: the start screen ceiling and the technique for the start theme.
- Final values for glass tints, blur and motion are tuned on the Honor 8C and an API
  31 or newer device.
