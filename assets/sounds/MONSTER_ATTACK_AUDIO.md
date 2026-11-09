# Monster attack cries

Retrieved 9 November 2026. Both recordings are offered under
[CC0 1.0](https://creativecommons.org/publicdomain/zero/1.0/); credit is kept although not required.

| Output (under `sounds/`) | Event | Original | Author / source |
| --- | --- | --- | --- |
| `monster_attack_damage.wav` | Monster is about to hurl its Damage (orange) fireballs; played once per volley | `monster-snarl-attack.ogg`, first snarl (0-1.25 s) | Darsycho, [Monster snarls](https://opengameart.org/content/monster-snarls), [download](https://opengameart.org/sites/default/files/monster-snarl-attack.ogg) |
| `monster_attack_sanity.wav` | Monster is about to hurl its Horror (blue) fireballs; played once per volley | `qubodup-GhostMoan05.wav`, slowed to 92 % | qubodup, [Ghost Monster Voice Moaning & Growling](https://opengameart.org/content/ghost-monster-voice-moaning-growling), [download](https://opengameart.org/sites/default/files/qubodup-GhostMoans.zip) |

Unmodified originals are in `source_assets/sounds/monster_attack/`. Rebuild with
`python tools/prepare_monster_attack_sounds.py` (requires `numpy` and `soundfile`):
trim, mono mix-down, boundary fades, -18 dBFS RMS with a 0.9 peak ceiling, 44.1 kHz PCM16.
