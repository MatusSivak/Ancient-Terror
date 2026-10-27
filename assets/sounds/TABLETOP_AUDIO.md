# Downloaded tabletop foley

Updated 27 October 2026. These cues replace the electronic setup, HUD, sheet and
spawn sounds with physical card, paper and chip recordings. No audio was generated.

## Downloads and licensing

All collections are CC0 1.0; the original download archives are preserved in
`source_assets/sounds/tabletop/` and contain the unchanged source audio.

- **Kenney, Casino Audio 1.1:** [author and license](https://kenney.nl/assets/casino-audio),
  [ZIP download](https://kenney.nl/media/pages/assets/casino-audio/2472606a04-1721639069/kenney_casino-audio.zip).
  The ZIP includes the author's `License.txt`.
  SHA-256: `f36250766ac5bc378c13708ddf12a23a8e54a3251f8d482c7536e51b5dbafa18`.
- **StarNinjas, 10 Book Page Flips:** [author and CC0 declaration](https://opengameart.org/content/10-book-page-flips),
  [ZIP download](https://opengameart.org/sites/default/files/book_flips_-_starninjas.zip).
  Credit: [StarNinjas](https://opengameart.org/users/starninjas).
  SHA-256: `0ff49c3677ea0e64544aeda3a0aa2ce71ee5d42b266da9e54421252a5ca5f20f`.
- **Kenney, RPG Audio:** [author and license](https://kenney.nl/assets/rpg-audio),
  [ZIP download](https://kenney.nl/media/pages/assets/rpg-audio/8e99002d76-1677590336/kenney_rpg-audio.zip).
  SHA-256: `6dbeaf8544da958d8f2adcb4a4a4b76c1ade34a05f8ab9edccd327da7375f38b`.
- **rubberduck, 80 CC0 RPG SFX:** [author and CC0 declaration](https://opengameart.org/content/80-cc0-rpg-sfx),
  [ZIP download](https://opengameart.org/sites/default/files/80-CC0-RPG-SFX_0.zip).
  SHA-256: `1c2f06ff4e8563b5b8b745b23cf213c1474142a69bb82bd8f5e10d9b3f7a7bbd`.

## Cue mapping

| Game event / WAV | Original recording |
| --- | --- |
| Setup/menu confirmation, `ui_button_click` | Kenney `card-place-1.ogg` |
| Left HUD buttons and dossier tabs, `ui_hud_click` | Kenney `chip-lay-1.ogg` |
| Investigator/reserve sheets, `ui_sheet_open` | StarNinjas `book_flip.5.ogg` |
| Sheet close, `ui_sheet_close` | Kenney `card-shove-2.ogg` |
| Dialog open / close (including investigator count) | Kenney `card-slide-1.ogg` / `card-place-2.ogg` |
| Show Ancient One / current Mystery | StarNinjas `book_flip.2.ogg` (same recording) |
| Close Ancient One / Mystery | StarNinjas `book_flip.6.ogg` |
| Investigator spawn | Kenney `chip-lay-3.ogg` |
| Clue token spawn | Kenney `chip-lay-2.ogg` |
| Expedition spawn, `expedition_spawn` | Kenney `card-shove-3.ogg` |
| Health/sanity token gain, `health_gain` | Kenney `chips-handle-1.ogg` |
| Clue / focus gain | Kenney `chips-handle-3.ogg` / `chips-handle-2.ogg` |
| Health/sanity, clue, focus HUD landing | Kenney `chip-lay-1.ogg`, `chip-lay-2.ogg`, `chip-lay-3.ogg` |
| Thrown dice slide into sorted order, `dice_sort` | Kenney `dice-grab-1.ogg` |
| Spell card shown/gained/zoomed, `ui_card_open_spell` | rubberduck `spell_01.ogg` |
| Artifact card, `ui_card_open_artifact` | rubberduck `item_gem_03.ogg` |
| Condition card, `ui_card_open_condition` | rubberduck `chain_02.ogg` |
| Asset card, `ui_card_open_asset` | Kenney RPG `handleCoins.ogg` |
| Monster movement, `monster_step_1`–`4` | Kenney RPG `footstep00.ogg`–`footstep03.ogg`; played every 0.38 s while monsters move, random variant, runtime pitch 0.72–0.84 |

Run `python tools/prepare_tabletop_sounds.py` with `numpy` and `soundfile` installed.
It reads directly from the ZIPs, removes surrounding silence, converts to 44.1 kHz
mono PCM16, applies 3 ms edge fades and adjusts volume. No pitch shifting, synthesis,
layering or artificial reverb is used. UI targets -23 dBFS RMS, gameplay -20 dBFS,
with a 0.80 peak ceiling. All cues are under 1.2 seconds and the Android SoundPool
decoded size limit. Existing preparation scripts preserve these replacements.

Runtime timing, user/fast-forward behavior and silent save restoration remain in
the existing sound hooks. Expedition placement now uses its own preloaded cue.
`GameplaySoundsTest` checks loading and WAV compatibility; `UiSoundsTest` checks
click, dialog and sheet playback behavior.
