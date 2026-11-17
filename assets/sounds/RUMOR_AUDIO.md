# Rumor arrival sound

`rumor_spawn.wav` now uses **magical_7.ogg** by **JaggedStone**, from
[Magic Spell SFX](https://opengameart.org/node/29772), released under
[CC0 1.0](https://creativecommons.org/publicdomain/zero/1.0/).
Downloaded on 17 November 2026 from the author's
[original OGG](https://opengameart.org/sites/default/files/magical_7_0.ogg).
The unmodified download is retained in `source_assets/sounds/rumor/magical_7.ogg`.

Rebuild with `python tools/prepare_rumor_sound.py` (numpy and soundfile).
The cue uses a lowered excerpt, a short reversed intake, a quiet echo and boundary
fades. Output: 1.65 seconds, 44.1 kHz, mono PCM16, peak ceiling 0.90 and target
-18 dBFS RMS. The decoded size is below Android SoundPool's 1 MB buffer budget.

This replaces the swish/thunder mix documented in NOV3_AUDIO.md.
The live map spawn callback plays it once across all three wrapped map copies.
Save restoration remains silent, and fast-forward keeps the existing audio policy.
