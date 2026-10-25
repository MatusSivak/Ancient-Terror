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
