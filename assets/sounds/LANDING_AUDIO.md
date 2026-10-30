# Map landing recordings

Downloaded from [Kenney Impact Sounds](https://kenney.nl/assets/impact-sounds),
licensed CC0, on 2026-10-29. The original ZIP and its license are retained in
`source_assets/sounds/landing`.

Original ZIP SHA-256: `029d734af1582474edf3a694d1b0cebc97c1c152f2f39fa34d4c2bafc5de77f8`.

| Game file | Source recording | Use |
| --- | --- | --- |
| map_token_impact.wav | impactWood_light_001.ogg | Clues and falling hazard tokens |
| map_investigator_impact.wav | impactSoft_heavy_001.ogg | Investigator footfall |
| map_gate_impact.wav | impactWood_heavy_000.ogg | Original gate landing; superseded by gate_resonance.wav in PRESENTATION_AUDIO.md |

Run `python tools/prepare_landing_sounds.py` with numpy and soundfile installed
to reproduce the mono 44.1 kHz PCM16 files. Only silence trimming, edge fades,
resampling and volume normalization are applied; these are individual recordings,
without synthesis or layering. The investigator footfall also receives a 2.6x
gain with a soft peak limit at 0.92 for a stronger, unclipped thump.
Playback starts on the same animation update as
ground contact and the settling dust, once across the three wrapped map copies.
Silent board restoration remains silent; fast-forward suppresses playback.
