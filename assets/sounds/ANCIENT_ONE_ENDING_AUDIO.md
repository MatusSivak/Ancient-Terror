# Ancient One defeat cue

`ancient_one_ending.wav` uses **gameoverdark.wav** by **Robin Lamb**, from
[Game Over Dark Short Music Clips](https://opengameart.org/content/game-over-dark-short-music-clips).
The author distributes this recording under [CC0 1.0](https://creativecommons.org/publicdomain/zero/1.0/).
Downloaded 19 October 2026 from https://opengameart.org/sites/default/files/gameoverdark.wav.

The unchanged source is in `source_assets/sounds/ancient_one_ending/`. Run
`python tools/generate_ancient_one_ending_sound.py` to reproduce the game asset:
10 seconds, 44.1 kHz mono PCM16, short boundary fades, normalized to a 0.92 peak.
Decoded audio stays below 1 MB for Android SoundPool.

The dark descending gong cue plays once when either illustrated Ancient One
defeat dialog (Azathoth or Yig) opens, and stops when dismissed. This replaces
Yig's ordinary Doom clock cue and Azathoth's earlier horror sting.
