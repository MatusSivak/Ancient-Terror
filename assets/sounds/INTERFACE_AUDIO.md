# Interface sound effects

The 27 October replacements for setup, HUD, sheets, dialogs, Ancient One and
Mystery panels are documented in [downloaded tabletop foley](TABLETOP_AUDIO.md).
That mapping supersedes those entries below; preparation scripts retain the new recordings.

Source and event audit: 24 October 2026. The 14 UI cues use CC0 recordings.

- Kenney, [Interface Sounds](https://kenney.nl/assets/interface-sounds),
  [OpenGameArt release](https://opengameart.org/content/interface-sounds),
  [download](https://opengameart.org/sites/default/files/kenney_interfaceSounds.zip).
  The six unmodified selected OGG files and bundled `License.txt` are retained in
  `source_assets/sounds/interface/`. The author supplies them under CC0 1.0.
- rubberduck's book recordings and PWL's bell recording are reused from
  `source_assets/sounds/gameplay/`. See [gameplay audio credits](GAMEPLAY_AUDIO.md)
  for the original source/download URLs and licenses.
- The encounter table pair also layers craigsmith's CC0 paper recording from
  `source_assets/sounds/typewriter/paper.mp3`; see its README for the source.

## Gesture coverage

| Gesture | WAV under `sounds/` | Source and character |
| --- | --- | --- |
| Enabled menu/setup/choice/confirmation button | `ui_button_click` | Kenney `click_003`, dry short click |
| HUD button or investigator dossier tab | `ui_hud_click` | Kenney `click_001`, slightly deeper click |
| Card closeup open / close; monster and rumor panels | `ui_card_open`, `ui_card_close` | rubberduck books 01 / 02, paper handling |
| Investigator, passport, reserve and discard sheets; Doom zoom | `ui_sheet_open`, `ui_sheet_close` | rubberduck books 03 / 04 + Kenney maximize/minimize 001 |
| Encounter table maximize / minimize | `ui_encounter_open`, `ui_encounter_close` | rubberduck books 01 / 04 + typewriter paper slide; no synthetic layers |
| Mystery sheet open / close | `ui_mystery_open`, `ui_mystery_close` | books 03 / 02; opening has a restrained discovery bell |
| Ancient One sheet open / close | `ui_ancient_open`, `ui_ancient_close` | books 04 / 02 + low bell layers |
| Dialog and action panel open / close | `ui_popup_open`, `ui_popup_close` | Kenney open/close 001, short air motion |

The Omen track keeps its existing ghostly zoom pair. Gaining/discarding a card,
typewriter stamps, dice, combat and endings keep their specific event sounds.

## Playback and processing

`UiSounds` defines paths and levels; `CustomAssetManager` preloads and tracks the
assets for reset/disposal. `SoundPlayer` keeps Android playback off the render
thread. Missing UI audio is skipped without synchronous asset decoding.

Button factories and bindings install one capture listener per button. Only a
completed pointer click plays; disabled/hidden controls, cancelled gestures,
programmatic checked-state changes and hover stay quiet. Nested controls emit one
click. Custom action and encounter controls sound only when their action is enabled.
Dialog visibility sounds include existing Back/Escape dismissal paths, with no
extra close cue for repeated calls to hide an already hidden dialog.

Explicit UI clicks and their synchronous panel transitions remain audible during
fast-forward; automatic panel transitions skip their cue during fast-forward.
Repeated dossier-tab selection and ordinary HUD counter refreshes are silent.

Rebuild with `python tools/prepare_interface_sounds.py` (`numpy`, `soundfile`).
Outputs are 44.1 kHz mono PCM16, 0.01–0.99 seconds, below the 1 MB decoded Android
SoundPool budget. The script trims silence, pitches/mixes sources, applies short
edge fades, and targets -20 dBFS RMS with a 0.85 peak ceiling. Runtime UI levels
are 0.65–0.85. The exact source layers and timings are in the script.

## Verification

`UiSoundsTest` covers duplicate binding, nested buttons, disabled/hidden controls,
cancelled gestures, programmatic updates, fast-forward/user input, sheet cue
selection and dialog visibility. `GameplaySoundsTest` verifies startup preloading,
WAV format/size and the token pickup/arrival pairs.

For a listening pass, use the menu and setup choices, HUD buttons, dossier tabs,
card closeups, reserve/discard sheets, mystery and Ancient One panels, Doom/Omen
zoom and action/encounter selections. Close dialogs by button and Back/Escape
where supported. Repeat with fast-forward enabled. Check speaker balance against
music and test token gain/arrival separately; automated checks do not replace
listening on the target device.
