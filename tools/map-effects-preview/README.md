# Map presentation preview

Run from the repository root with the project's JDK configured:

```shell
./gradlew -I tools/map-effects-preview/preview.gradle :desktop:previewMapEffects
```

The offscreen LWJGL runner saves PNGs to `build/map-effects-preview/` and checks:

- English and Slovak city/expedition labels, compact bounds, effect glyphs and camera-distance fading.
- Matching, repeated and cleared omen updates without restarting or retaining the gate pulse.
- Clue, investigator and monster reveals on all three map copies, including fast-forward.
- Single spawn callbacks, settled tokens, smoke cleanup and removal during an effect.

The related six-token Health/Sanity cascade checks are in `tools/vitality-loss-preview`.
