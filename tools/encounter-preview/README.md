# Encounter chooser preview

Compile the view module and build `:desktop:dist` once for the dependencies.
With JDK 17 or newer, run from `assets`:

```powershell
java '-Dui.language=en' -cp '../core/view/view-impl/build/classes/java/main;../core/constants/build/classes/java/main;../core/view/view-impl/src/main/resources;../desktop/build/libs/desktop-1.0-all.jar' '../tools/encounter-preview/EncounterPreview.java'
```

Use `-Dui.language=sk` for Slovak. This opens an offscreen renderer and writes
screenshots under `build/encounter-preview`. Checks label bounds, enabled/disabled
selection, scrolling, and reuse for a single encounter without loading a game.
The compiled view classes take priority over the desktop JAR, so later preview
runs only need view compilation and do not rewrite JARs used by an active game.
