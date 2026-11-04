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
| Strength | `dice_success_strength.ogg` (heavy punch) | `hit_03.ogg` | rubberduck, [100 CC0 SFX](https://opengameart.org/content/100-cc0-sfx) | `c8948294e7885ada74bcab6efb6c60613b74241cc58eacd18e81aa6bd33449b8` |
| Influence | `dice_success_influence.ogg` (coins dropping) | `item_coins_04.ogg` | rubberduck, [80 CC0 RPG SFX](https://opengameart.org/content/80-cc0-rpg-sfx) | `122b633be3e61f35363b28e7ca5f11257d9d7f2759547ba1441cf541a3e5792b` |
| Observation | `dice_success_observation.ogg` (camera shutter) | `camera.ogg` | FacadeGaikan, [CameraShudder](https://opengameart.org/content/camerashudder) | `239b0ccc800d8aedf4448fde008254a5f34a8595be94cc7a2fbad4569fd70adb` |
| Lore | `dice_success_lore.ogg` (old book pages) | `book_01.ogg` | rubberduck, [80 CC0 RPG SFX](https://opengameart.org/content/80-cc0-rpg-sfx) | `39b2d652dc8de9d4100ae8a1d23364077fff39bf1c90009b0c0d72af8c47d0d4` |
| Will | `dice_success_will.ogg` (gong) | `gong_01.ogg` | rubberduck, [100 CC0 SFX](https://opengameart.org/content/100-cc0-sfx) | `4780a38e91688b59899b3679cf3534b4b75654b45a177af9d9fb681c447f7f9a` |
| Plain roll (Reckonings...) | `dice_success_roll.wav` (success chime) | `bell.wav` | Fupi, [Correct Bell](https://opengameart.org/content/correct-bell) | `7b21371d23bcc5c56866b9bb32791b21bfce467edae05eef4bbc9d547da417a9` |
