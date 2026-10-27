# Bug reports reviewed on 27 October 2026

Synced with `origin/main` through `ad76a9c` before applying these changes. Reviewed
six active bug reports and no active crash reports. Attachments remain in the
ignored `build/report-review-oct27/` directory; they are not committed.

## Fixed

| Report | Change |
| --- | --- |
| `32287c77-d7bb-447b-9eb5-9867632b56eb` | Solved Mysteries are centered and acknowledged by tapping the card. Removed the separate solved-message/OK question. The card remains visible for its destruction animation, and repeated taps cannot resolve it twice. |
| `4768b52b-1057-4d6d-a3fc-66e949c27e2e` | Discard cards use the normal card zoom interaction. Removed the sheet-wide capture listener that dismissed the pile before a card could be inspected. The backdrop dismisses the sheet; card taps and scrolling keep it open. |
| `aa8d4804-f8ec-42d9-9210-e6342ac2a183` | Delivery Service filters the sender's offer to Items and clears token offers on both sides. Previously only the recipient's token offer was cleared, allowing the sender to deliver Clues and tickets. Actual owned token counts are preserved. |

## Reviewed and closed without changing game rules

- `0f960574-a4ed-4267-82c9-75dfd8374ce0`: explicitly a save-only submission, with no reported defect. Retained its attachment locally.
- `b7e89eed-d519-47f1-9248-d6f9944d7060` and `a4a33cb2-82a7-41e2-9391-7de92f50744c`: both saves already contain awakened Yig, three solved Mysteries, and Doom `-3` (23:45). The preceding discard/Delivery Service reports already contain awakened Yig with two solved Mysteries and Doom `-6` (23:30). Thus Yig was awake before the third Mystery; solving it must not reset the ongoing countdown to `-8` (23:20). The saved negative Doom survives serialization and loading, and the controller displays its positive remaining time correctly. Existing awakening tests verify the initial `-8` reset and preservation of an ongoing countdown on repeat awakening. The later phase log also records two further Doom advances after its attached checkpoint. No save/load or awakening-reset defect was reproduced from these attachments.

## Monster movement and combat

- Normal and epic monsters face their direction of travel with a short center-pivot flip, including map wrapping and fast-forward. Vertical travel preserves the last direction. Native sprite headings account for left-facing artwork, including Yig's Cultist.
- Combat monsters begin facing the investigator and return to that direction when idle or attacking. Mirrored epic glow and impact positions follow the sprite correctly.
- Combat verification exposed a fireball callback ordering bug: volley completion could run before the final hit began its health-token animation, and an old callback could release a later volley. Hit callbacks now register first, and completion callbacks capture their own volley.

## Validation

- 497 passing tests across action (23), event listener (133), controller (2), model (18), and view (321) modules.
- `previewGateCombat`: passed normal/fast-forward movement, animated facing, epic glow alignment, hit bounds, aimed-volley holds, attack recovery and interruption checks, alongside the existing gate checks.
- `previewReportFixes --args=--oct27`: passed real card taps through the discard event hierarchy, repeated opening and backdrop dismissal, centered solved-Mystery acknowledgement exactly once, and destruction completion at normal/fast-forward speed.
- The report preview also deserialized and reserialized three actual Yig report saves, then checked model state and clock-hand rotation. Synthetic states cover `-8`, `-6`, `-3`, and `0`.
- Inspected rendered screenshots for the centered Mystery, enlarged discard card, and combat heading changes. Desktop compilation passed as part of the previews. Android device playback was not performed.

## CI follow-up

The first GitHub run failed in the service module's `MysteryResolutionTest`, which
the original five-module local run omitted. Its controller mock still stubbed the
old dialog API, leaving `confirmSolvedMysteryCard()` null. Updated the fixture to
the card-confirmation API and added an asynchronous check that destruction and
queue continuation wait for the card tap without displaying an OK question.
The failure was reproduced locally before the correction. The full JVM command
`gradlew test -x :android:test --offline -PUSE_TEST_ADMOB_IDS=true` now passes
548 tests; Android tests and APK packaging are verified by GitHub's SDK-equipped
workflow.
