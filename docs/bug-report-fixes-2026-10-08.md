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
