# Rumor preview

Run `./gradlew -I tools/rumor-preview/preview.gradle :desktop:previewRumors`.

The offscreen runner renders all five rumor sheets in English and Slovak. It checks
the exact Mythos story/title match, glyph coverage, compact layout, normal and
fast-forward dissolution, single completion/dismissal callbacks, and disposal of
snapshots and tracked actions, including interruption. Screenshots go to
`build/rumor-preview/`; no save game or language preference is written.

Run `./gradlew -I tools/map-effects-preview/preview.gradle :desktop:previewMapEffects`
for the three wrapped storm markers, one arrival callback, silent restoration,
click routing, normal/fast/stalled frames, spiral removal and empty removal.
