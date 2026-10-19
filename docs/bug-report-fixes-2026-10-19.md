# Bug report fixes — 19 October 2026

Fetched every page of both Firebase collections. There are three new Android
1.7.17 bug reports and no crash reports. Report descriptions and attachments
are retained locally in ignored `build/report-review-oct19/`.

| Report | Change |
| --- | --- |
| `1ed0304d-fbbb-47e1-9811-fc9c2b693ea1` — overlapping investigator/travel zooms | Camera moves wait for the current movement to finish. Queued destinations are copied and their starting pose is calculated when they begin. The current actor publishes its final camera position and detaches before completing subscribers, preventing simultaneous writers and stale camera state in callbacks. |
| `b438d598-8e11-4c0b-abc7-305083b135ca` — train/ship ticket glyphs | Registered the full `train_ticket_down.png` and `ship_ticket_down.png` artwork as font glyphs, preserving their landscape proportions. Shared rule, card, question-answer, action-detail and investigator-ability text replaces complete ticket phrases, including English case/plural variants and Slovak forms. Counts and surrounding markup remain intact; typewriter flavor prose remains unchanged. Explicit train/ship ticket names are used in the Buy Ticket description so both choices render as glyphs. |
| `d3cd2c4c-efe2-44bb-ab10-4d7698d46f91` — Ancient One victory sound | Downloaded Robin Lamb's CC0 `gameoverdark.wav` and mastered a 10-second mono defeat cue. Both Azathoth and Yig's illustrated endings use it, replacing the old sting and ordinary Doom clock respectively. Included the source, license/source links and a reproducible standard-library mastering script. Playback remains once on opening and stops on dismissal. |

## Validation

- All 272 view tests passed, with no failures, errors or skipped tests. New
  coverage checks camera state before completion callbacks, retiring old camera
  actors, ticket glyph registration, artwork selection and proportions, rule/card/ability formatting,
  localized and wrapped phrases, word boundaries, quantities and flavor text.
- Desktop compilation passed.
- The `--oct19` production preview passed at normal and fast-forward speed:
  three overlapping requests leave only one camera actor active, completions
  occur once and in order, and the final pose is correct. It also exercises
  natural two-phase zooms, map wrapping, zero-duration moves and a move started
  by a completion callback.
- Ticket typewriter and English/Slovak rule/ability screenshots were rendered;
  the typewriter and Slovak screenshots were visually inspected. The full down-ticket images render without cropping or editing source art.
- Both production ending dialogs passed English/Slovak layout, background-click
  blocking, Continue and sound lifecycle checks. The audio double verifies the
  exact new asset path. The mastered WAV is 44.1 kHz PCM16 mono, 882,044 bytes,
  with zero-valued endpoints and a 0.92 peak; decoded samples fit under 1 MB.
- `git diff --check` passed.

Logs: `build/oct19-tests.log`, `build/oct19-preview.log`. Screenshots:
`build/report-fixes-preview/` and `build/ending-preview/`.

Reproduce the focused previews:

```powershell
.\gradlew.bat -I tools/report-fixes-preview/preview.gradle -I tools/ending-preview/preview.gradle :desktop:previewReportFixes --args="--oct19" :desktop:previewAzathothEnding --offline --console=plain
```

These are source changes, not a released build. Android gameplay and subjective
audio listening were not tested. Live report statuses and saved games were not
modified.

## Later review: Crown of the Serpent and clue-loss crash

Refetched all pages of both collections later on 19 October. The current set
contains four bug reports and one crash report; the three bugs above are already
fixed in the preceding commits. Two additional reports share the same saved game:

- Bug `09f6c535-8e79-4623-92f1-366852caf66e`: show the Crown mystery before its
  Will test and focus the investigator carrying the artifact.
- Crash `3a232aa9-8df7-4794-871a-080c828bfa99`: `ArrayIndexOutOfBoundsException`
  at `ContainerBar.getFullContainerPosition`, called by `TokenView.loseToken`
  during `LoseClueAction`.

The save has no active investigator at the end of the Mythos phase. The Spy owns
the Crown, two Clues and two Ally Assets; the Priest has no Clues. Crown resolution
previously changed only the model's active investigator, leaving the old HUD in
place. A reroll or mystery payment could therefore animate a Clue loss against
an empty displayed bar, accessing index -1.

Resolution now switches to the Crown holder, displays the mystery for
acknowledgement, and waits for the holder's camera focus and HUD refresh before
offering the Will test. The holder stays active through tests, payment and Ally
discards. A previous active investigator is restored together with their HUD;
an originally absent active investigator is restored to null.

Validation:

- The presentation regression failed before the fix. A real command-queue
  regression also reproduced the empty-HUD failure at the controller's animation
  boundary using the real investigator-switch, spend and lose-Clue actions.
- All 366 tests across services, event listeners and view passed (including
  unchanged cached view results), with no failures, errors or skipped tests.
  Desktop compilation passed.
- New queue coverage verifies that mystery acknowledgement and HUD animation
  block the test/payment, previous-investigator restoration, two/one/zero-Clue
  costs, and a reroll that leaves too few Clues to solve the mystery. Existing
  tests cover declining the test, failing it and having no Crown holder.
- The `--crown` production preview passed in English and Slovak language settings
  at both animation speeds. It checks card acknowledgement, camera position at
  the holder's saved location, actual HUD refresh and two Clue/one Focus discard
  animations completing exactly once. English/Slovak card screenshots and the
  holder's HUD screenshot were visually inspected.
- `git diff --check` passed.

Logs: `build/oct19-crown-before.log`, `build/oct19-crown-crash-before.log`,
`build/oct19-crown-crash-before.xml`, `build/oct19-crown-validation.log`, and
`build/oct19-crown-preview.log`. Screenshots: `build/report-fixes-preview/oct19-crown-*`.

```powershell
.\gradlew.bat -I tools/report-fixes-preview/preview.gradle :desktop:previewReportFixes --args="--crown" --offline --console=plain
```

Android device gameplay was not tested. Live report statuses and the attached
saved games were not modified.
