# Bug report fixes — 23 October 2026

Reviewed all four reports from the local viewer (no crash reports). Original
attachments are preserved in ignored `build/report-review-oct23/`.
All four live reports are now marked **Fixed**.

## Changes

- `3c44d9bf` — Crown of the Serpent now charges the card's displayed Clue cost,
  including earned Clue credit. Previously it calculated payment from the number
  of currently selected investigators, which shrinks when investigators are
  defeated before their replacements arrive. The attached four-investigator save
  and phase log show the resulting two-Clue payment. Defeats no longer discount
  the mystery.
- `a31157e0` — After the Will test, Crown checks the holder's remaining Clues
  before offering payment. Insufficient Clues produce a localized OK-only notice;
  no payment, Ally discard, or mystery progress occurs. The payment action still
  checks affordability when executing.
- `d78dc153` — Serpent's Nest groups Cultists by origin and moves each group
  together before spawning Yig. Cultists already at the destination stay there;
  other monsters are unaffected. Each group has one camera journey, and the
  command waits for every token and the camera before updating model locations.
- `0f2de2da` — Shortened the Priest's Boon notice to one line in English and
  Slovak. English: “Gate closed: the Priest gains a Boon.”

## Verification

- Service, event-listener, action, and view suites: **426 tests, zero failures,
  errors, or skips**. Desktop Java compilation passed.
- Crown regression coverage includes four-player cost with two survivors,
  insufficient funds, full payment, earned discounts, zero cost, declining,
  spending a Clue on a reroll, and restoring the prior investigator/HUD.
- Movement tests cover grouping by origin, excluding other monsters and Cultists
  already at the destination, single-monster compatibility, delayed model updates,
  and rejecting mixed-origin groups.
- Offscreen production preview passed using
  `:desktop:previewReportFixes --args=--oct23` with
  `-I tools/report-fixes-preview/preview.gradle`. Inspected rendered English/Slovak
  notices. Preview checks one-line layout, OK acknowledgement, synchronized
  movement, world wrapping, normal/fast-forward speeds, and stationary unrelated
  monsters. Screenshots: ignored `build/report-fixes-preview/oct23-*.png`.
- Suite XML results are under each module's `build/test-results/test/`.
  Successful preview output: `build/report-review-oct23/preview.log`.

Validation uses report-derived scenarios and production components, not a full
Android replay. No APK was installed or published.
