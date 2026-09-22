# Monster sheet preview

Render the real monster sheet without starting a game or changing save data:

```powershell
.\gradlew.bat -I tools/monster-sheet-preview/preview.gradle :desktop:previewMonsterSheet --offline
```

Uses an offscreen desktop window and saves screenshots to `build/monster-sheet-preview/`.
Checks every bundled monster for clipped labels, viewport height, health-token counts,
rule detachment/reattachment, long-rule scrolling, and reuse of a card with different rules.
See `BUILDING.md` for the Windows Java socket workaround if needed.
