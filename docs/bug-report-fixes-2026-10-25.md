# Bug and crash report fixes — 25 October 2026

Updated from `origin/main` to `4354ae8`, then reviewed all five new bug reports
and two new crash reports in the local viewer. Original report data, saves,
screenshots and logs remain in ignored `build/report-review-oct25/`.
All seven live reports are marked **Fixed**.

## Changes

- `00ce573c`, `c7602350` — Everyone Has a Price checks eligibility when each
  investigator's queued effect runs. No eligible condition means no offer.
  A sole condition is shown in full, named in the question, and can be declined.
  Multiple conditions are visible before accepting; the player chooses a card
  and confirms that specific discard. Accepted cards use the discard animation,
  Debt is gained before the selected condition is removed from the model, and
  the original active investigator/HUD is restored afterward. Blessed is never
  silently selected.
- `7912b2fc` — Monster defeat artwork fits within 30% of screen width and 45%
  of screen height instead of scaling every token by four. Ambushing monsters'
  idle squash actions are cleared before the defeat animation, and centering
  uses the artwork's height.
- `9b742a6f` — Token movement decides world wrapping from horizontal distance,
  matching the nearest-copy camera route. The old Euclidean-distance test sent
  the Buenos Aires–Himalayas monster the long way because of the latitude gap.
  Duration now uses the chosen displacement and is independent of token offsets.
- `8eba48bd` — Slovak rules preserve English glyph keywords for skills, tokens
  and Reckoning. Updated mystery/Ancient One/rumor/investigator rules, map labels,
  Yig special encounters and Yig research; surrounding prose stays Slovak.
- `aaaca32e` — An investigator's exit transition stops tracking a removed map
  token and finishes its fade. This covers the reported devour immediately after
  the transition signals completion, as well as removal during the exit.
- `dc6d7165` — Replaced reflective condition-back handler lookup with explicit
  constructor references for all 71 backs, including Paranoia. The reported
  handler existed in source but was unavailable to that running build; the new
  registry makes it a compile/package dependency. Both condition resolution and
  the research encounter that resolves Dark Pact use the same factory.

## Verification

- Event-listener, view, service and action suites: **490 tests, zero failures,
  errors or skips**. Coverage includes queued eligibility, declining Blessed,
  selecting among conditions, Debt/discard order and restoring the investigator;
  all map routes; defeat bounds; all condition-back mappings; and glyph parity
  between English and Slovak rules.
- Offscreen production preview: `:desktop:previewReportFixes --args=--oct25`
  with `-I tools/report-fixes-preview/preview.gradle`. Checked both animation
  speeds, removed investigator tokens, ambush completion/size, condition cards,
  confirmation/decline, and localized mystery glyphs. Group movement checks
  ordinary and wrapped routes plus Buenos Aires–Himalayas in both directions,
  matching camera/token direction, arrival, and stationary unrelated monsters.
  Inspected rendered screenshots under ignored `build/report-fixes-preview/`.
- `:desktop:build` and `:desktop:dist` passed. Inspected the distribution JAR:
  it contains the registry and every one of the 71 handlers, including all four
  Paranoia backs.
- Android debug build was attempted but could not start: the local
  `sdk.dir=C:\WORK\android_sdk` points to a missing directory and no alternative
  SDK was found in the standard local locations. No local Android replay or APK
  installation was performed. Desktop fixtures do not constitute a device replay.

Build/test output is in `build/report-review-oct25/`; Gradle XML results are in
the corresponding module `build/test-results/test/` directories.
