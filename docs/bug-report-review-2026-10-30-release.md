# Release follow-up: live reports reviewed on 30 October 2026

Queried the live `bugReports` and `crashReports` collections after pushing
`af09d40`. There were 21 reports, four still marked `new`. Reviewed those four
against their uploaded logs and current code. No additional distinct unresolved
crash was found in this snapshot. Report status was not changed remotely.

## Latest crash

`2d9ee869-eaa1-41ec-9791-b5637c0d0704`, created at 12:48:48 UTC on 30 October,
is the reported Flesh Ward combat crash on Desktop 1.7.17. Its log shows
`BEFORE_LOSE_HEALTH`, then `SHOW_ACTIVE_INVESTIGATOR`, then the nested
`CONFIRM_TEST`. Suspending combat tries to undo a preplayed health loss after
the HUD has already been rebuilt at full capacity, indexing token 4 of 4.

The production fix in `af09d40` associates preplayed loss with its original bar,
contents generation and value. A refreshed bar no longer receives a stale
rollback. Fixed-capacity HUD bars also clamp their values, and confirmation
errors propagate to the action subscriber.

Extended the desktop regression preview to call the real
`TestViewImpl.confirmTest` after a four-token HUD refresh, with an existing
combat panel. It verifies successful confirmation, panel interruption and
locking, unchanged health, capacity bounds, and completion of map-curtain
actions. This follows the failing call path in the uploaded log rather than
calling combat suspension directly.

## Other active reports

- `2bb1dc8d-beba-4aa0-b6e9-7c5aef5cf457`: the Mi-Go Brain Case listener cast
  crash is fixed in `4bd99eb`. Acquisition and reload regression tests cover
  its action menu, and the artifact registry test checks listener generic types.
- `5f9bcbc1-dab6-4fbe-87d1-e6d1d5fd2ca3` and
  `5feff725-0ef7-4648-a7a2-8f8095f4c819`: Serpent People defeat and Cursed
  are independent outcomes of Yig city research 16. The newer log confirms a
  real monster defeat. Failing Will still causes Cursed; defeating the monster
  grants the Clue. `4bd99eb` clarifies this text and fixes victory detection
  after the monster cup resets health. Existing tests exercise all combinations
  of the independent encounter outcomes, including defeat by Handcuffs.

## Validation

- Full JVM suite: 582 tests, zero failures, errors or skipped tests.
- Desktop compilation passed.
- Offscreen card/combat preview passed: nested spell confirmation, received-card
  effects, gate closure, usable-card badges, monster tear sound timing,
  normal/fast-forward completion and resource cleanup.
- Android packaging and device listening were not run locally; the configured
  Android SDK is unavailable on this machine.

Raw reports and attachments remain in ignored
`build/report-review-oct30-release/`. The pre-existing `assets/save.json` edit
is excluded from both release commits.
