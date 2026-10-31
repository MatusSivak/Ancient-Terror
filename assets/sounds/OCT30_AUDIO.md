# Travel, artifact, exhaustion and monster defeat audio

Walking and asset exhaustion now use the replacements in
[RELEASE_AUDIO.md](RELEASE_AUDIO.md); that page also credits the monster-sheet tear.

Prepared on 30 October 2026 with `python tools/prepare_oct30_sounds.py`
(`numpy` and `soundfile` required). The script supersedes the corresponding
outputs from `prepare_presentation_sounds.py`, `prepare_tabletop_sounds.py`
and `prepare_gameplay_sounds.py`; run it last when rebuilding all audio.

## Downloaded sources

- **Whistling train #1**, GlaneurDeSons, BigSoundBank, CC0:
  [author and license](https://bigsoundbank.com/whistling-train-1-s0225.html),
  [MP3 mirror](https://sfxjar.com/audio/0225.mp3).
  Retained as `source_assets/sounds/presentation/train-whistle-0225.mp3`.
- **Short Low Ship Horn 08**, SFXMint, CC0:
  [source and license](https://sfxmint.com/sounds/ship-horn-08),
  [WAV](https://sfxmint.com/dl/ship-horn-08.wav).
  Retained as `source_assets/sounds/presentation/ship-horn-08.wav`.
  SFXMint describes its library as generated/synthesized, reviewed audio.

Other layers come from the retained CC0 collections credited in
[PRESENTATION_AUDIO.md](PRESENTATION_AUDIO.md), [TABLETOP_AUDIO.md](TABLETOP_AUDIO.md)
and [GAMEPLAY_AUDIO.md](GAMEPLAY_AUDIO.md): Kenney RPG/Impact, rubberduck RPG,
PWL bell and the existing wave recording.

| Cue | Sound |
| --- | --- |
| `travel_rail` | Train whistle with subtle metal contact |
| `travel_sea` | Low ship horn with quiet waves |
| `travel_walk` | Four alternating, fuller grass footsteps |
| `ui_card_reveal_artifact` | Resonant bell and magical discovery flourish |
| `ui_card_open_artifact` | Shorter version for opening an existing artifact |
| `card_disable` | Book closing and a latch, at exhaustion-overlay start |
| `monster_defeat_fly` | Rising fire/magic sweep at animation takeoff Superseded by `prepare_category_sounds.py` (see CATEGORY_AUDIO.md). |
| `monster_defeat` | Layered magical explosion and heavy impact at fragmentation Superseded by `prepare_category_sounds.py` (see CATEGORY_AUDIO.md). |

Files are mono 44.1 kHz PCM16, 0.34–2.20 seconds, with boundary fades and
normalized levels. Each stays below Android SoundPool's decoded-size limit.
Gameplay cues use existing volume/mute, preload and fast-forward behavior;
restoring a disabled card does not play the exhaustion cue.
