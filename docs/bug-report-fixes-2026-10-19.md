# Bug report fixes — 19 October 2026

Fetched every page of both Firebase collections. There are three new Android
1.7.17 bug reports and no crash reports. Report descriptions and attachments
are retained locally in ignored `build/report-review-oct19/`.

| Report | Change |
| --- | --- |
| `1ed0304d-fbbb-47e1-9811-fc9c2b693ea1` — overlapping investigator/travel zooms | Camera moves wait for the current movement to finish. Queued destinations are copied and their starting pose is calculated when they begin. The current actor publishes its final camera position and detaches before completing subscribers, preventing simultaneous writers and stale camera state in callbacks. |
| `b438d598-8e11-4c0b-abc7-305083b135ca` — train/ship ticket glyphs | Registered cropped ticket-stub artwork as font glyphs. Shared rule, card, question-answer, action-detail and investigator-ability text replaces complete ticket phrases, including English case/plural variants and Slovak forms. Counts and surrounding markup remain intact; typewriter flavor prose remains unchanged. Explicit train/ship ticket names are used in the Buy Ticket description so both choices render as glyphs. |
| `d3cd2c4c-efe2-44bb-ab10-4d7698d46f91` — Ancient One victory sound | Downloaded Robin Lamb's CC0 `gameoverdark.wav` and mastered a 10-second mono defeat cue. Both Azathoth and Yig's illustrated endings use it, replacing the old sting and ordinary Doom clock respectively. Included the source, license/source links and a reproducible standard-library mastering script. Playback remains once on opening and stops on dismissal. |

## Validation

- All 272 view tests passed, with no failures, errors or skipped tests. New
  coverage checks camera state before completion callbacks, retiring old camera
  actors, ticket glyph registration/cropping, rule/card/ability formatting,
  localized and wrapped phrases, word boundaries, quantities and flavor text.
- Desktop compilation passed.
- The `--oct19` production preview passed at normal and fast-forward speed:
  three overlapping requests leave only one camera actor active, completions
  occur once and in order, and the final pose is correct. It also exercises
  natural two-phase zooms, map wrapping, zero-duration moves and a move started
  by a completion callback.
- Ticket typewriter and English/Slovak rule/ability screenshots were rendered;
  the typewriter and Slovak screenshots were visually inspected. Transparent
  ticket margins are cropped in the font renderer without editing source art.
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
