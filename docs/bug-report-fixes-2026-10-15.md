# Bug and crash report fixes — 15 October 2026

Reviewed the four new Android 1.7.17 reports and their screenshots/saved games.
Downloaded evidence is in ignored `build/report-review-oct15/`.

| Report | Change |
| --- | --- |
| Crash `1368125f-38ed-443f-9cba-44c02ada2715` | Combat cleanup now queues its before/after commands immediately, so Children of Yig, Vampire and Doppelganger can skip to `BEFORE_HIDE_COMBAT_TABLE`. Previously the target existed only after a deferred callback ran, causing the reported exception. The hide action retains a per-combat guard so Ghost's early cleanup cannot close the presentation twice. |
| Bug `f11dc62e-ba64-4353-8883-929ac55c486e` — update style | Restyled the ongoing-rumor card with the existing green/brass frame, cream text, muted flavor text, and separate rule/token panels. Preserved highlights, acknowledgement and countdown animations. Long Slovak section labels wrap within their column. |
| Bug `6056d0ba-38be-4378-ac66-496c86dc36bb` — Reckoning glyph | Rule text recognizes Reckoning regardless of capitalization and uses the existing colored glyph. Word boundaries and surrounding color markup are preserved; flavor prose is unchanged. |
| Bug `a5bf77c8-6ee1-447a-8219-4c17b8d76287` — defeated-investigator portrait | Encounter buttons now identify investigator portraits and use a circular head-and-shoulders crop, retaining the existing badge and encounter selection behavior. |

## Validation

- Reproduced the exact missing-command exception with a real command-queue
  regression before the fix (`build/oct15-crash-before.log`).
- All 351 tests in the affected suites passed: services 9, view 264,
  event listeners 78. The new combat tests exercise actual Children of Yig,
  Vampire and Doppelganger listeners, failed/successful Observation, skipped
  damage, waiting for the fade, and moving Children of Yig only afterward.
  The existing Ghost cleanup test confirms one physical hide.
- Desktop compilation and the `--oct15` production UI preview passed. The preview
  covers all five rumor cards in both languages, text bounds, acknowledgement,
  countdown, portraits, and glyph rendering at both speeds. Relevant screenshots
  were visually inspected, including the reported Nun portrait and Growing Madness.
- `git diff --check` passed.

Validation logs: `build/oct15-validation.log` and
`build/oct15-final-validation.log`. Screenshots: `build/report-fixes-preview/`.

Reproduce the UI preview:

```powershell
.\gradlew.bat -I tools/report-fixes-preview/preview.gradle :desktop:previewReportFixes --args="--oct15" --console=plain
```

These are local source fixes. No release was built, Android gameplay was not
tested, and live report statuses and saved games were not modified.
