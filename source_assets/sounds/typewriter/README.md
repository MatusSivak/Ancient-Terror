# Recorded typewriter keys

Author: Cassie-OrbitGames
Source: https://opengameart.org/content/typewriter-sounds
License: CC0 1.0 (https://creativecommons.org/publicdomain/zero/1.0/)
Downloaded: 2026-09-17. The source author describes these as phone recordings.

Original files typewriter2.wav through typewriter7.wav are used for six distinct
keys. tools/generate_typewriter_sounds.py downmixes to mono, trims leading silence,
retains up to 180 ms of each actual strike/release, normalizes peaks and fades edges.
No synthesized layers. Game files retain the source 22,050 Hz sample rate.

## Additional mechanical actions

All following recordings are CC0 1.0 and were downloaded on 2026-09-17:
- Spacebar: Joseph SARDIN / BigSoundBank, "Typewriter, space" (Hermes Precisa 305).
  https://bigsoundbank.com/typewriter-space-s2843.html
- Carriage return: Mihacappy, "typewriter_reset.wav".
  https://freesound.org/people/Mihacappy/sounds/868293/
- Paper removal and short roller/feed excerpt: craigsmith,
  "R20-03-Pull Paper Out of Typewriter.wav".
  https://freesound.org/people/craigsmith/sounds/483334/

The public high-quality MP3 previews are preserved as space.mp3, return.mp3,
and paper.mp3. tools/prepare_typewriter_actions.py selects separate excerpts,
downmixes/resamples to 22,050 Hz mono PCM, normalizes and fades edges. The line
feed is a short roller-ratchet excerpt from the paper recording, not a pitched key.

## Choice-button stamp

I.fekry, "traditional stamp.wav", CC0 1.0:
https://freesound.org/people/I.fekry/sounds/470710/
The public HQ preview is saved as stamp.mp3. A single impact (0.49-0.91 s)
is trimmed, normalized, faded and converted to 22,050 Hz mono PCM by
 tools/prepare_typewriter_actions.py. Plays once when a group of choices appears.
Carriage-return and line-feed clips are no longer built or played.
