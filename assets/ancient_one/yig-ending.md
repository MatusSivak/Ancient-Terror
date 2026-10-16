# Yig defeat ending

The dialog now uses `yig-ending-art-v2.png`; see [the wiki-inspired revision](yig-ending-v2.md).
The original artwork and its generation prompt below are retained for reference.

Generated with the built-in image generation tool using the existing awakened Yig
artwork as a character reference. The new asset is `assets/ancient_one/yig-ending-art.png`
(1536 × 1024, opaque PNG). Text is rendered separately for localization.

## Final generation prompt

Use case: stylized-concept. Asset type: original production game illustration for the Yig wins defeat dialog in Ancient Terror, landscape 3:2. Create a NEW full scene using the supplied Yig image ONLY as a character and painting-style reference, not as an edit target. Preserve his recognizable dark olive and bronze serpent head, glowing amber eyes, feathered ancient gold ceremonial crown, clawed humanoid arms, enormous coiled snake body. The victorious Father of Serpents towers above a ruined 1920s city at midnight, his immense coils winding through shattered stone buildings and a broken clock tower. Countless smaller serpents claim the abandoned streets beneath him; the last human lights fade into cold green mist. Ominous stillness after humanity's defeat, ancient vengeance fulfilled, no living heroes or gore. Rich hand-painted oil and ink cosmic horror illustration, moss green, blackened jade, tarnished gold, restrained amber highlights. Strong central Yig silhouette readable at 340 pixels wide, intricate atmospheric surroundings, dramatic depth. Opaque rectangular full-bleed background, no transparency. No text, letters, logos, border, UI, or title; all dialog lettering will be rendered by code. Original artwork, not a reproduction of any existing card.

## Dialog copy

Title: **Yig Wins!**

Subtitle: **The age of serpents**

The last bells fall silent. Serpents pour through the streets, and Yig coils around
the ruins of humanity.

His children have inherited the earth. Beneath a sky without dawn, the Father of
Serpents closes his eyes.

His vengeance is complete.

Footer: **Investigators lose the game.** The Continue button dismisses the ending
and lets the queued restart proceed. English and Slovak copy is in the UI bundles.
The dialog shares Azathoth's parchment layout and uses the existing Doom toll.

## Integration and preview

Awakened Yig reaching midnight queues the illustrated ending and waits for its
acknowledgement before restarting. Other losses routed through
`ancientOne.yig.endGame` also use this dialog.

Run the existing `tools/ending-preview/preview.gradle` task `:desktop:previewAzathothEnding`
to exercise both Yig and Azathoth through the production view in English and Slovak.
Screenshots are saved to `build/ending-preview/yig-en.png` and `yig-sk.png`.
