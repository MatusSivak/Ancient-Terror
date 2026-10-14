# Bug and crash report review — 14 October 2026

Fetched all current Firebase bug/crash report pages and inspected the four reports
and their attachments. All four still have status `new`; live report statuses were
not changed.

| Report | Outcome |
| --- | --- |
| Crash `80cb8a56-777e-4285-810f-4c68a47be221` (desktop, 13 October) | Fixed a null-pointer exception when Crown of the Serpent checks for progress between turns, with no active investigator. The check now reads the nullable investigator ID safely. If a Crown holder answers a question or takes a test, the previous active investigator—or the absence of one—is restored afterward. |
| Crash `33ec4af8-2366-4d93-aae7-8c120533ab6e` (Android, 14 October) | Same stack trace and root cause as the desktop report; covered by the same fix and regression tests. |
| Bug `6f6ca2fd-be6b-4ce7-ad13-0a6b3f5d6ebe` | Removed the requested combat-phase indicator from the top-left corner. The monster's status plate still highlights its horror/damage row. Removed the unused indicator class and translation keys. |
| Bug `38f7a0f5-f8bf-422c-a7fb-638290ae7c97` | Explicitly submitted only to provide a save file after solving a mystery, not to report a bug. No code change needed. |

## Validation

- Reproduced the reported exception with regression tests before the fix. Both
  tests exercising a missing active investigator failed at the reported call.
- After the fix, all 336 tests in the affected suites passed: event listeners 74,
  view 258, and services 4. Crown tests cover no owner, declining the optional
  test, failing it, and restoring either an existing investigator or none.
- Desktop compilation and the focused production combat preview passed. Visually
  inspected ordinary and epic monsters in the rendered screenshots; the separate
  phase overlay is absent. The preview also checks bounds and artwork cleanup.
- Updated stale preview expectations for clue credit, clickable monster artwork,
  and randomized fireball impact timing. Launch spacing and one-token-per-impact
  checks remain in place.
- The full preview passed its combat checks, then failed an unrelated map-walking
  assertion: maximum positive tilt was 2.216 degrees, below its 3-degree threshold.
  That assertion and map-walking production code remain unchanged.
- `git diff --check` passed.

To reproduce the focused visual checks:

```powershell
.\gradlew.bat -I tools/prompt-preview/preview.gradle :desktop:previewPrompts --args="--combat-display" --console=plain
```

Screenshots are in `build/prompt-preview/`. Downloaded report data is kept in
ignored `build/report-review-oct13/` (refreshed on 14 October). Test and preview
logs are `build/report-fixes-oct14-*.log`.

These are local source fixes. No release was built and Android gameplay was not
tested. Saved games were not modified.

## Follow-up: missing Serpent Crown offer

The Android crash report's saved unlock list did not include Serpent Crown.
The deck removed this locked artifact, so the mystery's availability check
silently skipped the substitution question. Serpent Crown is now a default
unlocked artifact because Yig's mystery requires it. The existing repository
migration merges it into new profiles, existing profiles, and imported saves;
it is also available outside Yig games, like other default artifacts.

Three new regression tests failed before the fix and passed afterward. They
cover fresh/legacy/imported unlock lists and deck save/load with the Crown
unowned, owned, or discarded. A further rule test confirms the choice on newly
drawn and restored mysteries, both answers, no offer while owned, an offer from
discard, and no offer after the mystery listener is removed. The artifact
repository tests, full event-listener suite, and full model suite passed.
Logs: `build/crown-availability-before.log` and
`build/crown-availability-after.log`. No save files were edited.

## Follow-up: latest map and Healing Words reports

Refreshed both Firebase collections on 14 October. There are now four bug reports
and no crash reports: three new reports below, plus the previously reviewed
save-only report `38f7a0f5-f8bf-422c-a7fb-638290ae7c97`.

| Report | Outcome |
| --- | --- |
| `2de4aafa-67c7-4ab3-b652-77de27267fac` — highlight epic monsters on the map | Added a persistent silhouette outline. Following the requested style change, both map and combat outlines pulse red. The map outline follows the monster's movement, breathing, rotation and opacity, remains after the reckoning body tint is removed, and leaves no separate map actor behind. |
| `5e76b331-84da-4ce2-892d-3357ea47b18d` — defeated investigator displays delayed hourglass | Defeat now clears the manually drawn hourglass and its actions immediately. Later delayed notifications cannot recreate it. Covers both health and sanity defeat, including defeat during the hourglass entrance animation. |
| `5813abaf-6af5-4a39-8c06-2114ff7bfe9f` — Healing Words gives no recovery while Poisoned | Expected behavior under the implemented card rules. Poisoned says resting recovers no Health or Sanity; Healing Words increases recovery from that same rest. `PoisonedListener` disables both recovery channels, so `RestData` returns zero regardless of which listener modifies it first, and `RestActionAction` skips recovery. No gameplay change. |

The Healing Words save contains the spell with back 1 (no additional healing on
success) but no remaining Poisoned condition. It is a checkpoint, not an action
history, so it cannot reconstruct the reported roll or the condition's removal.
The conclusion above is based on the card text and execution path, not a replay
of that particular turn.

Validation:

- All 259 view tests passed, including a regression for delayed notifications
  after either type of defeat.
- The focused `--map-reports` desktop preview passed. Visually inspected living
  delayed, defeated, moving and faded map actors in its screenshots.
- Desktop and preview compilation passed; `git diff --check` passed.
- Log: `build/latest-report-validation.log`. Screenshots:
  `build/prompt-preview/map-reports-*.png`.

Changes are local source fixes; no release was built and Android rendering was
not tested. Live report statuses and saved games were not modified.

The red outline pulses smoothly between 40% and 100% opacity every approximately
2.1 seconds in both views, multiplied by the existing visibility/fade opacity.
The map and combat focused previews both passed and their bright/dim screenshots
were visually inspected. Logs: `build/epic-red-map-preview.log` and
`build/epic-red-combat-preview.log`.

## Evening follow-up: Hall of Fame, menus, and reroll crash

Refreshed all Firebase report pages again: three bug reports and one crash report
remain. The save-only and Healing Words reports were already reviewed. The new
reports are the Crown clue-cost request and the reroll crash below.

| Issue | Result |
| --- | --- |
| Hall of Fame investigator deaths | New victory records store defeated/devoured investigator names. The screen shows a skull directly beside each recorded casualty. The list includes devoured investigators and defeated investigators whose tokens were already recovered; living investigators Lost in Time and Space remain survivors. Older records lack this information and explicitly show that survival details are unavailable. |
| `Yep, Yig is coming` clock `23:80` | Read the live record and verified Doom is `-4`. Awakened Yig uses negative Doom for time remaining. Hall of Fame now applies the gameplay interpretation and displays `23:40`. The stored record does not need editing. Other clock values are normalized to valid hours/minutes. |
| Card Collection Back remains checked | Made only the Back button momentary. Category tabs remain selectable. Verified repeated navigation and re-entry. |
| Crash notice redesign | Replaced the chalkboard with the framed green/brass report style, readable message, recovery guidance and a Close game button. English and Slovak text are included. Acknowledgement calls the existing crash handler exactly once; the existing automatic report capture remains in place. |
| Bug `c4a6f26a-13e5-4ed4-8e44-ac3b6acd1b85` | Crown of the Serpent now displays the actual total clue payment instead of “1 Clue per investigator.” It includes existing clue-cost reductions and never displays a negative cost; its one-step mystery progress remains unchanged. |
| Crash `1f8237d6-a11d-43c6-b180-f76904b86e15` | Reproduced `IndexOutOfBoundsException` when a two-die reroll is requested with one available die. Rerolls now select at most the available dice. An empty pool completes without starting an animation. Automatic selection uses a sorted copy, preserving the original test's dice order and identities; explicit selections still work. |

Validation:

- The focused reroll tests reproduced the crash before the fix.
- All affected unit suites passed: actions 15, model 18, view 263 (296 total).
- Tests cover the actual Hall of Fame upload JSON shape, legacy/empty casualty
  records, living off-board survivors, negative Yig Doom, ordinary clock values,
  reroll bounds/selection/scoring, Crown player counts 1–8, and restored clue credit.
- Desktop integration compiled. The `--latest` production UI preview passed;
  visually inspected Hall of Fame, collection, both localized crash dialogs,
  and the Crown's numeric cost. No live crash was deliberately triggered.
- `git diff --check` passed.

Logs: `build/oct14-reroll-before.log`, `build/oct14-evening-tests.log`,
`build/oct14-evening-preview.log`. Screenshots are in
`build/report-fixes-preview/`. The skull's reproducible vector-shape renderer is
`tools/ui-assets/create_skull.py` (Pillow).

These changes are local. No release was built, Android gameplay was not tested,
and no saved games, live report statuses, or Hall of Fame records were modified.

## Nun action follow-up

The Nun's existing passive adds 1 to each die rolled for a Boon or Bane
condition effect, including rerolls, capped at 6. It does not apply to an
ordinary test merely because she possesses Blessed or Cursed. For example,
Blessed's reckoning discards on a raw 1 only (modified to 2), while Cursed's
reckoning discards on a raw 3–6 (modified to 4–6).

Comfort already supports choosing among all Madness conditions belonging to
the selected investigator. Its selection title now identifies that investigator,
and every chosen card is displayed with the owner's portrait and name before
the discard animation. Only after acknowledgement does the card leave that
owner's inventory and the next success become available. Multiple successes
can remove multiple Madness conditions from the same investigator or be split
between investigators on any space.

Validation: all 93 event-listener and action tests passed. Added coverage for
choosing the second of two Madness cards, the displayed card and owner,
waiting for acknowledgement, preserving unrelated conditions, repeated
discards from one owner, exhausting available Madness, failed tests, and Done.
Log: `build/oct14-nun-tests.log`. No Android gameplay session was run.
