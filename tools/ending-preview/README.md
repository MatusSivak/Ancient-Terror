# Azathoth ending preview

Run from the repository root:

```powershell
.\gradlew.bat -I tools/ending-preview/preview.gradle :desktop:previewAzathothEnding --offline
```

The offscreen desktop renderer uses the real dialog, assets and fonts. It writes English and Slovak screenshots to `build/ending-preview/`, checks text heights, verifies that background clicks do not dismiss the card, and verifies that Continue removes it and completes the waiting game action. A silent audio double verifies one playback on opening and a stop on dismissal. It does not load a saved game. The UI language preference is restored after rendering.
