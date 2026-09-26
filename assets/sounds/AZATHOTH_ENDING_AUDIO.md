# Azathoth ending sound

`azathoth_ending.wav` is a 9.5-second mono master of “HorrorSting1” by shelbyshark, downloaded from [Freesound](https://freesound.org/people/shelbyshark/sounds/513332/). The source is released under CC0, so it can be copied, modified, and distributed commercially without attribution. The original preview is kept in `source_assets/sounds/azathoth_ending/HorrorSting1.mp3`; `tools/generate_azathoth_ending_sound.py` trims the deep impact and its rumbling tail, soft-saturates it for loudness, normalizes to a 0.98 peak and fades the end.

The game uses 44.1 kHz, 16-bit PCM and plays the cue once at full volume when the defeat dialog opens, stopping it when the dialog is dismissed.
