# Game presentation fixes — 2026-10-29

- [x] Louder investigator landing thump, with peak limiting.
- [x] Keep 30 ms between health/sanity departures, including fast-forward and slow frames.
- [x] Match received tickets to the actual HUD slot position and dimensions; resolve queued tickets lazily.
- [x] Different, louder gate landing sound: a resonant metal/stone impact.
- [x] Replace spinning card gains with an illuminated edge reveal, light sweep, sparks and settling motion.
- [x] Improve Spell, Asset, Artifact and Condition display sounds with distinct layered recordings.
- [x] Destroy rumor cards with the mystery-style effect; verify existing monster sheet destruction and reuse.
- [x] Distinct sea, rail and walking travel sounds, including ticket travel.
- [x] Different Action button sound and a reused HUD sound when selecting a travel destination.
- [x] Show investigator special actions during selection, using glyphs; make room for both ability and action.
- [x] Preload all Ancient One selection portraits and the lock before the gameplay queue to reduce the transition stall.
- [x] Restore Hunting Byakhee's spawn explanation (movement plus delay).
- [x] Increase ordinary and epic monster map size by 10%.
- [x] More sinister monster movement sound, using lower stone steps and creature layers.
- [x] Hide dice/test UI behind Flesh Ward and Instill Bravery prompts; focus the caster after Yes and preserve the pending roll on No.

Validation completed 2026-10-30:

- 476 view and event-listener tests, zero failures/errors; includes defensive spell acceptance/decline and Byakhee effect order.
- Rendered ticket landing bounds for both slots at normal/fast/changing speed and large deltas, including moving HUD targets and queued gains.
- Rendered health/sanity departure timing, including fast-forward and slow frames, and effect completion/cleanup.
- Rendered map spawn, shadow, wrapped-copy, callback and cleanup checks.
- Rendered four card gain types, normal/fast-forward, cleanup, Flesh Ward prompt visibility/restoration and investigator action text for the roster.
- Rendered rumor, mystery and monster destruction, sheet reuse, snapshot cleanup and interrupted effects.
- Inspected investigator selection, Flesh Ward prompt, card reveal and rumor destruction screenshots.

Audio sources and reproducible processing are recorded in `assets/sounds/PRESENTATION_AUDIO.md` and `assets/sounds/LANDING_AUDIO.md`. The selection loading change removes the late asset requests; device-specific transition timing remains unmeasured.
