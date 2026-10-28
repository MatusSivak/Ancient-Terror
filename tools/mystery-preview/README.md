# Mystery card preview

With the project JDK configured, run from the repository root:

```shell
./gradlew -I tools/mystery-preview/preview.gradle :desktop:previewMysteries
```

The offscreen LWJGL runner exercises all 34 mystery descriptions in English and Slovak through the actual sheet opening and click-to-close flow. It checks centering over faded gameplay controls, natural text wrapping, font glyph coverage, bounds with a live question, recentering after the question disappears, and exactly one dismissal callback.

Screenshots are saved under `build/mystery-preview/`. The runner restores the language without writing preferences and does not load or modify a saved game.
