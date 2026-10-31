# Detained overlay

`assets/map/detained-bars.png` was generated with the built-in imagegen tool on 31 October 2026.
Its PNG alpha preserves the open spaces between bars. It is drawn over investigator tokens,
settles with a short rattle, follows token movement/fading and disappears when Detained is lost.

Prompt:
> Use case: stylized-concept. Asset type: transparent game status overlay for an investigator portrait in a 1920s cosmic horror board game. Create a single front-facing iron jail grille: five thin vertical dark weathered iron bars joined by two narrow horizontal iron rails, with small rivets. Rectangular portrait aspect ratio 2:3, bars cover full height. Actual transparent empty spaces between bars, no background, no wall, no floor, no people, no shadows outside the metal, no text. Subtle steel highlights and restrained rust, painterly realistic detail, clear silhouette legible when reduced to 70 by 100 pixels. Keep about 75 percent of area transparent so portrait remains visible underneath. Entire grille visible with narrow transparent outer margin.

`assets/sounds/investigator_detained.wav` is a cinematic jail door slam trimmed from the CC0
recording "JailDoorClose" by TurtleLG (Freesound 80449) by `tools/prepare_category_sounds.py`
(credits in `assets/sounds/CATEGORY_AUDIO.md`). It plays once
for a newly gained condition, remains silent on loading, and respects sound/fast-forward settings.
