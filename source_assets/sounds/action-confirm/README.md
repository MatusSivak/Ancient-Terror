# Action confirmation audio

The six WAV originals come from the inventory folder of artisticdude's
[RPG Sound Pack](https://opengameart.org/content/rpg-sound-pack), released under
[CC0](https://creativecommons.org/publicdomain/zero/1.0/).
[Download the source archive](https://opengameart.org/sites/default/files/rpg_sound_pack.zip).

| Action | Recording and edit |
| --- | --- |
| Travel | Heavy cloth with a gravel footstep |
| Rest | Soft cloth with a relieved sigh |
| Focus | A single lowered, ringing metal tone |
| Acquire Assets | A coin toss |
| Buy Ticket | A paper shuffle followed by a metal punch |
| Trade | Two wooden taps, the second reversed and quieter |

Additional recordings are credited in [release audio](../../../assets/sounds/RELEASE_AUDIO.md)
and [category audio](../../../assets/sounds/CATEGORY_AUDIO.md).
Run `python tools/prepare_action_confirm_sounds.py` with numpy and soundfile
to rebuild the six mono 44.1 kHz PCM16 game cues.
