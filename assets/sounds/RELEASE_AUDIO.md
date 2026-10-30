# Walking, phase and card recordings

Downloaded 30 October 2026. These mappings supersede travel, phase and exhaustion
cues in the earlier audio notes. Originals are retained unmodified in
`source_assets/sounds/release/`.

| Recording | Author / license | Source and download | Runtime use |
| --- | --- | --- | --- |
| `gravel.ogg`, `leaves01.ogg` | TinyWorlds, CC0 1.0 (edited from pdsounds.org) | [Different steps](https://opengameart.org/content/different-steps-on-wood-stone-leaves-gravel-and-mud), [ZIP](https://opengameart.org/sites/default/files/%5Bkdd%5DDifferentSteps_0.zip) | `travel_walk`, `travel_depart`, `travel_arrive`: alternating ground crunch/scuff |
| `gong_01.ogg` | rubberduck, CC0 1.0 | [100 CC0 SFX](https://opengameart.org/node/85570), [ZIP](https://opengameart.org/sites/default/files/100-CC0-SFX_0.zip) | Identical encounter cue for action, encounter and mythos phases |
| `paper_ripped_-_1.mp3` | Luckius, CC0 1.0 | [Various Paper Sound Effects](https://opengameart.org/content/various-paper-sound-effects), [MP3](https://opengameart.org/sites/default/files/paper_ripped_-_1.mp3) | `monster_sheet_tear`, once at the visible split |
| `paper_sound_-_1.mp3` | Luckius, CC0 1.0 | Same collection, [MP3](https://opengameart.org/sites/default/files/paper_sound_-_1.mp3) | `card_disable`: brief paper fold/rustle |

Previous auditioned sources remain for provenance: IgnasD's `Ejimas1.ogg`
([source](https://opengameart.org/content/step-sound-walking),
[ZIP](https://opengameart.org/sites/default/files/Ejimas1.zip)), and rubberduck's
`bell_01.ogg` / `gong_02.ogg` from the collection above. These are no longer used.

Rebuild with `python tools/prepare_release_sounds.py` (numpy and soundfile).
Processing: mono mixdown, silence trim, pitch/resampling, short edge fades,
level matching, and 0.88 peak ceiling. Outputs are 44.1 kHz mono PCM16, below
Android SoundPool's 1 MB decoded-size limit. Train and ship departures retain
their transport-specific audio. Fast-forward and existing sound settings are respected.

License: [CC0 1.0](https://creativecommons.org/publicdomain/zero/1.0/).
