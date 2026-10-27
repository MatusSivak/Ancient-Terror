# Monster defeat preview

Run the real animation in an offscreen desktop window, without loading a saved game:

```powershell
.\gradlew.bat -I tools/monster-defeat-preview/preview.gradle :desktop:previewMonsterDefeat --offline
```

Checks the visible enlargement and pause, fragment separation, transparent ambush
entry, normal/fast-forward completion, speed changes, and cleanup of actors and GPU
snapshots after completion or early removal. Includes ordinary, epic, tall and wide
portraits. PNG animation frames and key screenshots go to `build/monster-defeat-preview/`.
See `BUILDING.md` for Java setup.

Monster explosions use 32 tumbling shards sharing one GPU snapshot, with a pressure
ring and embers. The preview checks fragment separation and snapshot cleanup.

Clue, focus, train and ship ticket spending can also be previewed:

```powershell
.\gradlew.bat -I tools/monster-defeat-preview/preview.gradle :desktop:previewResourceSpend --offline
```

Checks lazy HUD updates, empty slots, burst/completion timing, normal speed,
fast-forward, speed changes, large time steps and interruption. Frames are saved
under `build/resource-spend-preview/`.
Tickets use their two torn artwork pieces with a gentle pull and fall, without
flashes or particles. Gate checks also verify spin continuity at the collapse transition.
Use `:desktop:previewGateClose` with the same init script to check gate closing on its own.

Gate closing and combat perspective use the same offscreen runner:

```powershell
.\gradlew.bat -I tools/monster-defeat-preview/preview.gradle :desktop:previewGateCombat --offline
```

Checks all three gate colours, the shudder, the vortex collapsing into rising mist,
fast-forward, early removal, deferred closing, and the visible copy at each map wrap.
Combat checks ordinary and epic near/far movement, transformed artwork bounds,
nameplates, aiming, relative attack lunges, ground shadows, interruptions and removal.
PNG film strips and key frames go to `build/gate-combat-preview/`.

Combat also checks left/right movement facing, entering and idling toward the
investigator, animated side turns, mirrored epic glow, invertible hit transforms,
and fireball scatter inside mirrored artwork. The existing depth film strips show
both directions and the flip between them.
