# Gameplay sound effects

Source and event audit: 24 October 2026. All **25 gameplay WAV files** are derived from
the following recordings, offered by their authors under
[CC0 1.0](https://creativecommons.org/publicdomain/zero/1.0/).
Credit is retained here even though attribution is not required.

| Author / collection | Original files used | Source |
| --- | --- | --- |
| rubberduck, **80 CC0 RPG SFX** | `book_01`–`book_04`, `item_gem_01`, `item_gem_04`, `creature_monster_03`, `creature_roar_03`, `creature_die_01`, `spell_02` (OGG) | [Author's page](https://opengameart.org/content/80-cc0-rpg-sfx), [download](https://opengameart.org/sites/default/files/80-CC0-RPG-SFX_0.zip) |
| Ogrebane, **Teleport Spell** | `teleport.wav` | [Author's page](https://opengameart.org/content/teleport-spell), [download](https://opengameart.org/sites/default/files/teleport.wav) |
| PWL, **Bell dings/chimes** | `bell_ding1.wav` | [Author's page](https://opengameart.org/content/bell-dingschimes), [download](https://opengameart.org/sites/default/files/bell_ding1.wav) |

Unmodified selected originals are in `source_assets/sounds/gameplay/`. Rebuild with
`python tools/prepare_gameplay_sounds.py` (requires `numpy` and `soundfile`).
The script trims silence, adjusts pitch/duration, mixes selected bell layers,
adds boundary fades and normalizes toward -16 dBFS RMS with a 0.92 peak ceiling.
Outputs are 44.1 kHz mono PCM16, 0.18–1.80 seconds, each well below the 1 MB
decoded Android SoundPool limit. The script contains exact processing settings.

## Event coverage added

| Event / animation | Output WAV (under `sounds/`) | Source / sound direction |
| --- | --- | --- |
| Clue appears; expedition becomes active | `clue_spawn` | PWL bell, slightly lowered discovery chime |
| Clue collected from map or pool | `clue_gain` | rubberduck gem 04 + PWL bell; brighter pickup |
| Clue reaches its HUD slot | `clue_land` | rubberduck gem 01 + short high PWL bell; crisp contact |
| Focus token gained | `focus_gain` | PWL bell, soft lower chime |
| Focus token reaches its HUD slot | `focus_land` | rubberduck gem 01 + short lower PWL bell |
| Clue/focus spent; clue removed from map | `resource_spend` | rubberduck gem 01; short physical token tick |
| Travel ticket gained / spent or discarded | `ticket_gain`, `ticket_spend` | rubberduck books 01 / 02; paper handling |
| Travel ticket reaches its HUD slot | `ticket_land` | rubberduck book 02 + gem 01; brief paper/token contact |
| Card gained / discarded; rumor card removed | `card_gain`, `card_discard` | rubberduck books 03 / 04; neutral paper handling, including harmful conditions |
| Gate opens / closes | `gate_open`, `gate_close` | Ogrebane teleport, forward / reversed |
| Regular / epic monster appears | `monster_spawn`, `epic_monster_spawn` | rubberduck monster 03 / lowered roar 03 |
| Monster defeated, including ambushes | `monster_defeat` | rubberduck creature death 01 |
| Mystery progress increases / mystery solved | `mystery_progress`, `mystery_solved` | PWL bell, single step / ascending layered resolution |
| Skill improvement gained / lost | `skill_gain`, `skill_loss` | PWL bell, ascending pair / lower tone |
| Rumor storm or vortex appears | `hazard_spawn` | rubberduck spell 02, lowered magical burst |
| Investigator appears during setup or replacement | `investigator_spawn` | rubberduck book 01 + PWL bell + Ogrebane teleport; placement with a short rising swell |
| Investigator defeated / devoured | `investigator_defeat`, `investigator_devoured` | PWL low dissonant bells / Ogrebane reversed teleport |
| Mythos phase begins | `mythos_phase` | PWL low bell warning |

Clue, focus, and ticket pickups now have distinct short arrival cues when they
reach the HUD; the longer gain cue plays as the token is taken or appears in the
center. Arrival is sequenced after the movement and before the HUD counter changes.
Health and sanity retain their shared gain/loss/departure cues with higher levels;
`token_land.wav` now has a midrange wooden contact audible on smaller speakers.
These four original, sample-free effects are rebuilt by `tools/generate_token_sounds.py`.

The gameplay mix was raised from the previous 0.35–0.58 playback levels to
0.70–0.95, alongside louder source normalization. Travel/highlight and HUD track
zoom levels were also raised. Music volume remains governed by its existing control.

## Existing coverage retained

- Health/sanity recovery and loss, including combat's already-animated losses.
- Investigator highlighting, travel departure and arrival.
- Dice impacts, grid swaps, rerolls, pickups, explosions and test transitions.
- Fireball launch/impact, monster hit and health tearing.
- Doom advance/retreat and Omen track zooms.
- Typewriter text, stamped buttons and paper dismissal.
- Ancient One defeat endings and phase/background music.

## Timing and quiet paths

`GameplaySounds` owns cue paths and volumes. `CustomAssetManager` preloads and
tracks them for existing reset/disposal behavior. Playback uses `SoundPlayer`'s
Android background dispatch and short duplicate suppression. Missing audio is
silent; event playback never waits for decoding.

Spawn callbacks run on the center actor's animation after the camera move and
asset load. The three wrapping map copies produce one cue. Restoration calls
(`justSpawnClue`, `justPlaceGate`, `justPlaceMonster`, `justPlaceInvestigator`, `justSpawnStorm`,
`justAddVortex`, `justPlaceExpeditionToken`, `loadDefeatedInvestigator`) use silent
paths. All new cues, including queued ones, honor fast-forward at playback time.

HUD refreshes, decorative monster wandering, repeated highlights, and capped/no-op
rest and focus actions stay quiet. Opening existing card panels and other UI
gestures have their own [interface cues](INTERFACE_AUDIO.md). Rumor removal uses
a neutral paper cue because the same animation can follow success or failure.

## Verification

`GameplaySoundsTest` checks queued timing, fast-forward changes, failed preceding
events, missing audio, token pickup/arrival pairs, startup preloading, and every
packaged gameplay, token and interface WAV's format/size.
For an in-game listening pass: spawn clues and both monster types; collect a map
clue and a pool clue; gain/spend focus and tickets; open/close a gate; gain/discard
cards; improve/lose a skill; advance/solve a mystery; trigger a storm/vortex and
investigator placement/defeat/devouring. Listen separately for each pickup and
HUD arrival. Repeat during fast-forward, then reload a save
containing these map objects to check restoration stays quiet.
