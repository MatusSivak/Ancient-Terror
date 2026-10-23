# Bug report fixes — 23 October 2026, evening

Reviewed the three latest reports and their screenshots, logs, and saves from
the local viewer. Original attachments remain in ignored
`build/report-review-oct23-evening/`.
All three live reports are marked **Fixed**.

## Changes

- `8d87a48c-8a67-4c6d-8a5f-d221d29283bf` — Glimmer of Hope uses shorter
  choices with Focus and Clue glyphs. The four available choices fit in a
  balanced two-row layout, preserving their original game effects.
- `dd1a1e61-9add-4d9c-a1d6-e2c7f629bf84` — The World Fights Back uses Health
  and Sanity glyphs in its recovery buttons. Question buttons preserve glyph
  colors, measure visible text correctly on first use, and wrap long choices
  within the available width.
- `0d11cc59-5387-4d2a-98c0-7471f5c05d5e` — Debt's background remains visible
  while the paper hides for a dice test and while results return to the paper.
  Artwork retention now starts before the hide animation; releasing the dice
  view leaves the final transition to the owning paper. Ordinary paper hiding
  and resolution cleanup still remove the background.

## Verification

- Service, event-listener, action, and view suites: **436 tests, zero failures,
  errors, or skips**. Desktop Java compilation passed.
- The offscreen `:desktop:previewReportFixes --args=--oct23-evening` preview
  invokes both production mythos handlers and checks every choice's result,
  glyph substitution, bounds, compact first-use sizing, and question cleanup.
  Inspected both rendered prompts.
- `:desktop:previewCardResolution` checks Debt's background on every frame
  through delayed test setup and result handoff at normal and fast-forward
  speeds. Nested card backgrounds, hide/resume/finish cleanup, dice glyphs,
  and doom audio checks also passed.
- Preview commands use `-I tools/report-fixes-preview/preview.gradle`.
  Screenshots are in ignored `build/report-fixes-preview/`; logs are in
  `build/report-review-oct23-evening/`.

Validation uses report-derived scenarios and production components, not a full
Android replay. No APK was installed or published.
