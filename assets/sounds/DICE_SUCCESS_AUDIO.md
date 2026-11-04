# Dice success sounds

Added 4 November 2026. When a thrown die (first roll or reroll) lands and turns green,
`DiceRoller` plays a cue chosen by the tested skill. Rolls without a skill (single
die rolls such as Reckoning effects) stay silent.

All recordings are CC0 1.0. Unchanged originals are in `source_assets/sounds/dice_success/`.
Rebuild with `python tools/prepare_dice_success_sounds.py` (requires `numpy` and `soundfile`):
silence trim, optional length cap with fade-out, mono 44.1 kHz PCM16, -20 dBFS RMS, 0.80 peak.

| Skill | Output WAV (under `sounds/`) | Original recording | Author / source | SHA-256 of original |
| --- | --- | --- | --- | --- |
| Strength | `dice_success_strength` (gunshot) | `gunfire_sfx.wav` (stored as `gunfire.wav`) | iamoneabe, [Random gunfire SFX](https://opengameart.org/content/random-gunfire-sfx) | `a60ba5477bea3b7e1047597fee3280a1e981d63e637649763187713ff98475be` |
| Influence | `dice_success_influence` (coins dropping) | `coin_drop.wav` | Vinrax, [Coin Drop](https://opengameart.org/content/coin-drop) | `2965ecdd82bfefca244eab761597d9409b8ec8dcac18345c125e98b4abf5e21f` |
| Observation | `dice_success_observation` (camera shutter) | `photo.ogg` | themightyglider, [Camera](https://opengameart.org/content/camera) | `029dfcb981fdf375fb0ae4657962b3d470e1a073932c3d3aefb15d3d9ba38670` |
| Lore | `dice_success_lore` (arcane shimmer, capped at 1.1 s) | `magical_2.ogg` | JaggedStone, [Magic Spell SFX](https://opengameart.org/content/magic-spell-sfx) | `84a70f63f3f21e5ba4a48285b760a663d422ad3feddba2f3cbadd94b76cdc52e` |
| Will | `dice_success_will` (clear bell) | `pleasing-bell.wav` | Spring Spring, [Pleasing Bell Sound Effect](https://opengameart.org/content/pleasing-bell-sound-effect) | `3c851939ccf9146e49c1be0272386fdca1b1442bb034a5e6dcdfb3e0e144a411` |
