# Dice success sounds

When a thrown die (first roll or reroll) lands and turns green, `DiceRoller` plays a cue:
the combat cue during combat tests, otherwise the cue of the tested skill. Rolls without a
skill (single die rolls such as Reckoning effects) play the generic roll cue.

**Currently every non-combat cue (all skills and plain rolls) plays `dice_success_will.wav`**; only combat
has its own sound. The other files below are kept unused so a cue can be switched back by changing its
file name in `GameplaySounds.Cue`.

The files are the downloaded originals, unmodified (only renamed), CC0 1.0 unless noted below. To replace a
sound, overwrite the file under `assets/sounds/`; any OGG, MP3 or 16-bit PCM WAV works (keep the extension, or change the file name in
`GameplaySounds.Cue`). Playback volume is set per cue in `GameplaySounds.Cue`.

| Roll | File (under `sounds/`) | Original file | Author / source | SHA-256 of original |
| --- | --- | --- | --- | --- |
| Combat | `shotgun_reload.wav` | user-provided 44.1 kHz 32-bit float stereo WAV, converted to mono 16-bit PCM (libGDX 1.9.8 limit / packaged-cue format) and normalised; otherwise unchanged | source/licence not recorded | `e177ae18f486c71bbe0e40697c51a8e552ee9e8269dac34e6ba0202d86f74d9d` |
| Strength | `dice_success_strength.wav` | user-provided 48 kHz 24-bit WAV, converted to 16-bit PCM (libGDX 1.9.8 limit); otherwise unchanged | source/licence not recorded | `5c30f5deee94e0871fc9757524be0cffc7a1f695254301c0fdfceb2a7a079d18` |
| Influence | `dice_success_influence.wav` (coins) | user-provided, unchanged | source/licence not recorded | `a63830730303c1942184aca536ab8132ff3c1c62e1f53cd8130a6cf4e633549e` |
| Observation | `dice_success_observation.wav` | user-provided, unchanged | source/licence not recorded | `5c747feb7d44b8d84653edf6cf7bcf628d415ddd97474184226fd271a083e15e` |
| Lore | `dice_success_lore.wav` | user-provided, unchanged | source/licence not recorded | `2910d5cec528d864e3a700a9cd9fe5eb026bb38f13ee4740e07421444844f73d` |
| Will | `dice_success_will.wav` | user-provided, unchanged | source/licence not recorded | `d8745bed16ad18586c5176d058e8b3b8062a6a5ec62d2874e4980063ec91bb40` |
| Plain roll (Reckonings...) | `dice_success_roll.wav` (success chime) | `bell.wav` | Fupi, [Correct Bell](https://opengameart.org/content/correct-bell) | `7b21371d23bcc5c56866b9bb32791b21bfce467edae05eef4bbc9d547da417a9` |

The former combat gunshot (user-provided 96 kHz 32-bit float WAV, SHA-256 of original `d9b3a5316181aafb92af2e8b57e8371fc4bc369d2141d05a946a0bc2f137b3d9`, source/licence not recorded) now plays when a fireball launches from a die in combat: it is packaged as `sounds/fireball_launch.wav`, converted to mono 16-bit PCM at 44.1 kHz and normalised.

Each fireball launch picks 50/50 between that gunshot and `sounds/fireball_launch_shotgun.wav`: hyperix6, "Shotgun Fire", Freesound #660299 (https://freesound.org/s/660299/), file `660299__hyperix6__shotgun-fire.wav` (48 kHz 32-bit float stereo, SHA-256 `98a980336ba64b5ed616515e229c7de88daad31a1b67b4969cd9bd5e2668cc68`, licence not recorded), converted to mono 16-bit PCM at 44.1 kHz and normalised.

Monster fireballs (and other non-dice fireballs) keep the original generated whoosh, now `sounds/fireball_launch_monster.wav` (`tools/generate_combat_sounds.py`). Dice fireball launches are never merged, so every shot sounds even when fired milliseconds apart.
