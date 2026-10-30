# Card destruction preview

Run from the repository root with Java configured as described in `BUILDING.md`:

```powershell
.\gradlew.bat -I tools/card-destruction-preview/preview.gradle :desktop:previewCardDestruction --offline
```

Uses an offscreen desktop window and silent audio stubs, without loading or changing
a saved game. Exercises the actual asset discard confirmation and current-Mystery
resolution, including hidden cards, fast-forward, changing speed mid-animation,
long Mystery text, and opening the next Mystery on the reused sheet.

Checks that snapshots preserve position, rotation and colour under a transformed
parent, that capture does not advance actions, and that completion waits for the
fragments to clear. Every effect owns one GPU snapshot and releases it both on
completion and early removal. The unit geometry test also checks that the jagged
pieces cover the complete card across portrait, landscape and tall layouts.

PNG film strips are written to `build/card-destruction-preview/card/`, `mystery/`
and `tall-mystery/`.

Also exercises the real monster-sheet destruction (visible and initially hidden),
normal/fast-forward playback, callback ownership, GPU cleanup and reopening the
reused sheet. Monster frames are saved under `monster/`. An investigator refresh
check verifies that post-encounter HUD updates leave camera position and zoom
untouched in both normal and fast-forward playback.

Also verifies received-card reveal and collection: rotation, deferred completion,
double-click protection, effect cleanup, and fast-forward. Gate closure checks
keep the vortex opaque during spin-up and complete exactly once after the mist.
Inspection frames are `release-card-spawn.png`, `release-card-flight.png`,
`release-gate-spin.png`, and `release-gate-dissipate.png` in the preview directory.

Combat regression checks rebuild a four-token HUD before a spell interruption,
then verify rollback cannot overfill it. Usable exhausted cards keep their badge
hidden through asynchronous artwork loading and drag/zoom duplication, while
inventory cards retain it. An instrumented audio stub checks the monster-sheet
tear plays once at the split and stays silent during fast-forward.
