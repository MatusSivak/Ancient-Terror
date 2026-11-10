# Map presentation preview

Run from the repository root with the project's JDK configured:

```shell
./gradlew -I tools/map-effects-preview/preview.gradle :desktop:previewMapEffects
```

The offscreen LWJGL runner saves PNGs to `build/map-effects-preview/` and checks:

- English and Slovak city/expedition labels, compact bounds, effect glyphs and camera-distance fading.
- Gates matching the current omen light nearby terrain in red, green, or blue across the map seam. Dormant gates leave the terrain dark and turn slowly, and removing a gate clears its light.
- The current investigator lights the terrain around them with a white spotlight; other investigators do not.
- Matching gates pulse continuously. Repeating the same omen preserves the pulse, and clearing or changing to a different color stops it.
- Clue, investigator and monster landings from above on all three map copies, including fast-forward.
- Smoke starts at impact, with tokens settling at their exact map positions.
- Single spawn callbacks, settled tokens, smoke cleanup and removal during an effect.
- New gates arrive as a comet crashing into the location: hidden before impact, impact sound, smoke, flash and a short camera shake, then the gate grows from the crater. This covers fast-forward, quiet placement on load and clearing mid-effect (camera restored).

The related six-token Health/Sanity cascade checks are in `tools/vitality-loss-preview`.

Use `:desktop:previewOctoberReports` with the same init script for the 30–31 October
reports: gate closing below monsters, normal/epic monster discards, and the Detained
bars overlay on gain, load, discard and defeat. It also checks the matching gate
pulse at spawn, the monster disappearance puff, and Poisoned vapour/skull
on gain, load, cure and defeat (including coexistence with Detained and transformed
stands). Both animation speeds are checked,
including all three map copies and exactly-once completion. Screenshots go to
`build/report-review-oct31/`. If the downloaded Curse-crash save is present there,
the preview loads its condition deck and verifies duplicate and exhausted draws
through the production action. Otherwise it uses a synthetic fixture.
