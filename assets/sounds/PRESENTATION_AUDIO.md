# Presentation and travel cues

Prepared by `tools/prepare_presentation_sounds.py` from retained CC0 recordings:

- Kenney [Impact Sounds](https://kenney.nl/assets/impact-sounds), [RPG Audio](https://kenney.nl/assets/rpg-audio), and [Casino Audio](https://kenney.nl/assets/casino-audio).
- rubberduck [80 CC0 RPG SFX](https://opengameart.org/content/80-cc0-rpg-sfx), retained under `source_assets/sounds/tabletop` (see TABLETOP_AUDIO.md).
- transitking, submitted by qubodup: [Water Waves](https://opengameart.org/content/water-waves), CC0. `wave_01_cc0-11505__transitking__wavesound.flac` downloaded 2026-10-29 and retained as `source_assets/sounds/presentation/wave_01.flac`.

| Cue | Recordings / treatment |
| --- | --- |
| gate_resonance | Lowered heavy metal impact, stone debris, short echo; louder than the old wooden landing |
| ui_card_reveal_spell | spell_02 and bookOpen, short echo |
| ui_card_reveal_artifact | item_gem_01 and item_gem_04, shimmering echo |
| ui_card_reveal_condition | chain_03 and quiet creature_misc_03, dark echo |
| ui_card_reveal_asset | Leather handling and metal latch |
| ui_action_click | chip-lay-3 and a quiet metal click |
| monster_prowl_1–4 | Lowered stones_01–04 with quiet creature_monster_01–04 layers |
| travel_walk | Three grass footsteps |
| travel_rail | Rhythmic paired metal clacks |
| travel_sea | Recorded wave with a quiet wooden creak |

The script documents exact source names, layering, pitch, timing and levels. Outputs are mono 44.1 kHz PCM16, at most 2.4 seconds, with edge fades and a soft peak limiter. Cues use new filenames so older preparation scripts do not overwrite them. Sound buffers are preloaded and satisfy Android SoundPool's decoded-size limit.
