# Dice success sounds

When a thrown die (first roll or reroll) lands and turns green, `DiceRoller` plays a cue:
the combat cue during combat tests, otherwise the cue of the tested skill. Rolls without a
skill (single die rolls such as Reckoning effects) play the generic roll cue.

The files are the downloaded originals, unmodified (only renamed), CC0 1.0 unless noted below. To replace a
sound, overwrite the file under `assets/sounds/`; any OGG, MP3 or 16-bit PCM WAV works (keep the extension, or change the file name in
`GameplaySounds.Cue`). Playback volume is set per cue in `GameplaySounds.Cue`.

| Roll | File (under `sounds/`) | Original file | Author / source | SHA-256 of original |
| --- | --- | --- | --- | --- |
| Combat | `dice_success_combat.wav` (gunshot) | user-provided 96 kHz 32-bit float WAV, converted to 16-bit PCM (libGDX 1.9.8 limit) with gain scaled to fit full scale; otherwise unchanged | source/licence not recorded | `d9b3a5316181aafb92af2e8b57e8371fc4bc369d2141d05a946a0bc2f137b3d9` |
| Strength | `dice_success_strength.wav` | user-provided 48 kHz 24-bit WAV, converted to 16-bit PCM (libGDX 1.9.8 limit); otherwise unchanged | source/licence not recorded | `5c30f5deee94e0871fc9757524be0cffc7a1f695254301c0fdfceb2a7a079d18` |
| Influence | `dice_success_influence.wav` (coins) | user-provided, unchanged | source/licence not recorded | `a63830730303c1942184aca536ab8132ff3c1c62e1f53cd8130a6cf4e633549e` |
| Observation | `dice_success_observation.wav` | user-provided, unchanged | source/licence not recorded | `5c747feb7d44b8d84653edf6cf7bcf628d415ddd97474184226fd271a083e15e` |
| Lore | `dice_success_lore.wav` | user-provided, unchanged | source/licence not recorded | `2910d5cec528d864e3a700a9cd9fe5eb026bb38f13ee4740e07421444844f73d` |
| Will | `dice_success_will.ogg` (gong) | `gong_01.ogg` | rubberduck, [100 CC0 SFX](https://opengameart.org/content/100-cc0-sfx) | `4780a38e91688b59899b3679cf3534b4b75654b45a177af9d9fb681c447f7f9a` |
| Plain roll (Reckonings...) | `dice_success_roll.wav` (success chime) | `bell.wav` | Fupi, [Correct Bell](https://opengameart.org/content/correct-bell) | `7b21371d23bcc5c56866b9bb32791b21bfce467edae05eef4bbc9d547da417a9` |
