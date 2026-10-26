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

Gate closing and combat perspective use the same offscreen runner:

```powershell
.\gradlew.bat -I tools/monster-defeat-preview/preview.gradle :desktop:previewGateCombat --offline
```

Checks all three gate colours, the shudder, the vortex collapsing into rising mist,
fast-forward, early removal, deferred closing, and the visible copy at each map wrap.
Combat checks ordinary and epic near/far movement, transformed artwork bounds,
nameplates, aiming, relative attack lunges, ground shadows, interruptions and removal.
PNG film strips and key frames go to `build/gate-combat-preview/`.
