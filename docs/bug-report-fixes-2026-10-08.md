# Bug report review — 8 October 2026

Reviewed all six manual reports and the crash report in the project's Firestore
collections, including their screenshots and saved games. Fixes below are local
source changes; they have not been released or verified on an Android device.

| Report ID | Finding and change |
| --- | --- |
| `a636c5c8-f494-4ed9-94d5-ffa3e262dcdb` | The crash stack dereferenced a missing active investigator during a die roll. Board-wide rolls now return the normal die value without applying investigator abilities. Sister Mary's condition bonus remains covered by tests. |
| `5ec2d49b-c68a-40b1-892f-b276e8ac3505` | Map dragging could retain a pressed pointer after an interrupted gesture, and always read pointer 0's position. New touches recover stale drag state, dragging reads the correct pointer, disabling gestures cancels their state, and pinch state resets between gestures. This addresses a code path consistent with the report; device reproduction remains outstanding. |
| `59c3ea33-6871-4ca1-86b6-185352af2a55` | Yig location choices opened a question even for one distinct destination (the screenshot shows Tunguska as the only option). That destination now resolves automatically. Equally valid destinations remain a player choice. |
| `f1f9e2b3-5f04-44b7-b991-a3cf3a349642` | K'n-yan's guardian ambush ended without a follow-up explanation. Both combat outcomes now return to the encounter paper and explain that this failure branch ends without advancing the mystery. Defeating the guardian does not incorrectly award mystery progress. |
| `d6c5bcf2-24f5-49c0-94dc-c95dc5bfeded` | Two gate-selection callers supplied no question title. They now state what the selected gate is for; the mythos prompt also states its Doom consequence. The selection data has a default title, and these callers avoid an unanswerable chooser when no gates exist. |
| `2173c403-cef6-4dba-b242-4c895a7417c1` | Omen opening/closing sounds already existed, but fast-forward also silenced manual HUD inspection even though that animation runs at normal speed. Manual inspection now plays its cues with fast-forward enabled. Automatic accelerated zooms still suppress cues. Android playback needs device confirmation. |
| `44d9fce9-5b57-4a67-b1f8-c095457d4f2b` | Interpreted “dint use place eldritch tken” as a wording request based on its screenshot. Changed K'n-yan's reward and payment prompt to “Advance the Active Mystery by 1.” The underlying progress action remains intact and tested. If the report meant missing progress instead, that interpretation needs clarification. |

## Validation

- Action implementation: 5 tests passed.
- Event listener implementation: 52 tests passed.
- View implementation: 240 tests passed.
- Desktop Java compilation succeeded.
- `git diff --check` passed.
- Android compilation could not start: `local.properties` points to the missing
  SDK directory `C:\WORK\android_sdk`.

Added regression coverage for investigator-free dice rolls, preserved Sister Mary
bonuses, interrupted touch recovery, single/tied/empty location choices, both
guardian outcomes, gate prompt consequences and empty gate lists, and manual omen
audio with fast-forward enabled.

## Follow-up reports

Reviewed all six new reports, their screenshots, and attached saves. These requests
supersede the earlier nearest-expedition tie behavior and mystery wording above.

| Report ID | Change |
| --- | --- |
| `fbc24153-3e9f-4eb7-80c4-637292a04def` | Clues moving to a nearest expedition now choose automatically, including a random selection among equally near destinations. Other travel choices retain their existing rules. |
| `741f90ae-e482-4e10-8294-a21b700dc4ce` | K'n-yan now says “Advance the Active Mystery” without “by 1,” including the clue-payment question. |
| `bed09abd-c0a8-4811-b2dd-6edf032de4dd` | Yig's Ally discards randomly select an eligible Ally, including when several are available. The selected card is shown for acknowledgement; no selection question appears. Multiple discards select from the remaining possessions after each discard completes. |
| `9c05882d-e42f-4c91-be57-44af545d6c5b` | Cursed removal automatically targets the sole eligible investigator. With several eligible investigators it opens the standard investigator picker restricted to those investigators, including delayed or detained investigators. |
| `f901a906-0ac4-4ef7-a8ba-c4f881a3133c` | Optional opening Yig research encounters present Yes/No directly on the encounter paper. Accepting applies costs before rewards; declining follows the existing refusal outcome. An unavailable condition offer retains its explanatory confirmation. |
| `b51824ce-c91d-4189-8bbd-0ab2c165d29e` | Replaced the defeated investigator's 49 animated puzzle overlays on each of three map copies with a static red or blue portrait and border tint. Removal uses a short fade that completes the command queue. The separate devouring effect remains intact. |

Follow-up validation: 63 event-listener tests and 242 view tests passed, including
automatic selection, sequential Ally discards, all seven optional opening choices,
cost-before-reward ordering, constant draw counts, preserved actor opacity, and
defeated-actor removal completion. Desktop Java compilation and `git diff --check`
passed. Android device performance and gameplay still require device verification;
no Android build was produced because the configured SDK is missing.

The report viewer's pending session fix was also verified: 19 Python tests and
5 browser tests passed. Opening the bare localhost URL now obtains the active
server's session, and Windows prevents two viewer processes sharing the same port.
