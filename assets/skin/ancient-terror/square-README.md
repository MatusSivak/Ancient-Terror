# Investigator count buttons

`square_normal.png` and `square_pressed.png` are transparent 256 x 256 bronze-and-jade button backgrounds generated with built-in imagegen. Prompts are saved in `square-prompts.json`.

The startup investigator selector uses these assets for all eight choices. Numbers are rendered by the game, not baked into the textures. `AncientTerrorMenuStyles` loads them through the asset manager and creates nine-patches with 48-pixel fixed edges, scaled to 50 x 50 logical units. The row has six units between buttons and fits within the new dialog's content padding.

The startup menu's rectangular buttons share `AncientTerrorMenuStyles.button()` with the in-game menu and use the existing `buttons.atlas` assets.
