# November 3 audio changes

All new recordings are CC0 1.0. Retrieved 3 November 2026; original downloads
are retained in `source_assets/sounds/nov3/`. Runtime files are mono 44.1 kHz
PCM16 WAV, each below Android SoundPool's decoded buffer limit.

| Source | Author and license | Runtime use |
| --- | --- | --- |
| [Om.wav](https://freesound.org/people/ApeironMedia/sounds/445246/) · [HQ MP3](https://cdn.freesound.org/previews/445/445246_9221316-hq.mp3) | ApeironMedia, CC0 | `focus_gain`: 2.0–5.2 second excerpt, with a 2.1 second cosine fade |
| [Magic Spell SFX](https://opengameart.org/content/magic-spell-sfx) | JaggedStone, CC0 | `health_gain`: magical_1; `sanity_gain`: magical_3; `omen_change`: magical_4 |
| [Vintage cash register](https://freesound.org/people/Tompalompa/sounds/475915/) · [WAV excerpt mirror](https://slapalert.com/assets/sounds/register.11d20a22.wav) | Tompalompa, CC0; excerpt supplied by [Slap Alert](https://slapalert.com/sounds) | `ui_reserve_open`: crank, bell and till opening |
| [Swishes sound pack](https://opengameart.org/content/swishes-sound-pack) · [ZIP](https://opengameart.org/sites/default/files/swishes.zip) | artisticdude, CC0 | `card_collect`: three fading swish-7 passes; `monster_discard`: swish-10 with short echoes; `rumor_spawn`: swish-10 with thunder |

Magic originals: [magical_1](https://lpc.opengameart.org/sites/default/files/magical_1_0.ogg),
[magical_3](https://lpc.opengameart.org/sites/default/files/magical_3_1.ogg),
[magical_4](https://lpc.opengameart.org/sites/default/files/magical_4_1.ogg).

Existing CC0 recordings, with source links and authors in [CATEGORY_AUDIO.md](CATEGORY_AUDIO.md),
are reused for the shared health/sanity HUD landing (el_boss's puzzle-piece placement),
the louder clue landing (Breviceps's wooden thud), the stronger gate landing
(space portal plus wooden pontoon impact), and rumor thunder (BigSoundBank).

Processing: silence trim, mono conversion, DC removal, level matching, soft peak
limiting and short edge fades. Focus, magical and storm cues have longer release
fades. Clue/gate impacts target -11 dBFS before soft limiting; the card flight
targets -24 dBFS and plays at 38% volume. Gameplay cues are suppressed during
fast-forward; an explicit reserve-opening gesture stays audible.

Regenerate with `python tools/prepare_nov3_sounds.py` (numpy and soundfile required).
Run this **after** the legacy generators, which also produce several of these names.
No downloads are needed to regenerate.
