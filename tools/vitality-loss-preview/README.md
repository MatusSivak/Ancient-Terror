# Health and sanity loss preview

Run with Java 22 and the project's existing Gradle cache:

```powershell
.\gradlew.bat -I tools/vitality-loss-preview/preview.gradle :desktop:previewVitalityLoss --offline
```

Uses an offscreen LibGDX desktop window and the real token artwork. Frames go to
`build/vitality-loss-preview/{health,sanity,monster}`. Sound is silent in the preview.

Checks the swell, fracture callback and completion ordering at normal speed and
fast-forward; changing speed mid-effect; removing the effect early; six-token
investigator cascades through GameViewImpl; zero losses and empty bars; monster
damage, duplicate impacts, healing, overkill and reinitialization. Every completed
or interrupted effect must release its fast-forward action.

The full combat regression preview also covers fireballs and investigator loss
accounting:

```powershell
.\gradlew.bat -I tools/prompt-preview/preview.gradle :desktop:previewPrompts --offline
```
