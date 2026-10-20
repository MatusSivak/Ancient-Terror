# Ancient One ending preview

Run from the repository root:

```powershell
.\gradlew.bat -I tools/ending-preview/preview.gradle :desktop:previewAzathothEnding --offline
```

The offscreen desktop renderer uses the real Azathoth and Yig dialogs, assets and fonts, routed through the production view. It writes English and Slovak screenshots to `build/ending-preview/`, checks text heights, verifies that background clicks do not dismiss the card, and verifies that Continue removes it and completes the waiting game action exactly once. A silent audio double verifies one non-looping Music playback on opening, then stop and disposal on dismissal; it rejects use of the asynchronous SoundPool path. It does not load a saved game or write language preferences.
