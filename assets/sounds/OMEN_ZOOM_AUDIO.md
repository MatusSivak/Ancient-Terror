# Omen track zoom sounds

Both sources are released under CC0 and were downloaded from Freesound. The original previews are kept in `source_assets/sounds/omen_zoom/`, and `tools/generate_omen_zoom_sounds.py` trims, fades and loudness-matches them.

- `omen_maximize.wav`: “Spectral Ghost Whisper Breeze Whoosh” by brktkrgll ([Freesound 856169](https://freesound.org/people/brktkrgll/sounds/856169/)). The swell peaks as the omen track reaches the center.
- `omen_minimize.wav`: “Ghostly Whoosh - Horror Game UI” by TommasoMotteran ([Freesound 850767](https://freesound.org/people/TommasoMotteran/sounds/850767/)). A quick ghostly hit that decays as the track returns to the HUD.

The cues play when the omen track zooms in or out, both during omen changes and when the player taps the omen HUD button. They are skipped in fast-forward.
