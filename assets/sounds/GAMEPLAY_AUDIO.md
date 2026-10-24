# Gameplay sound effects

Source and event audit: 24 October 2026. All **21 new WAV files** are derived from
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
adds boundary fades and normalizes toward -20 dBFS RMS with a 0.85 peak ceiling.
Outputs are 44.1 kHz mono PCM16, 0.24–1.80 seconds, each well below the 1 MB
decoded Android SoundPool limit. The script contains exact processing settings.

## Event coverage added

| Event / animation | Output WAV (under `sounds/`) | Source / sound direction |
| --- | --- | --- |
| Clue appears; expedition becomes active | `clue_spawn` | PWL bell, slightly lowered discovery chime |
| Clue collected from map or pool | `clue_gain` | rubberduck gem 04 + PWL bell; brighter pickup |
| Focus token gained | `focus_gain` | PWL bell, soft lower chime |
| Clue/focus spent; clue removed from map | `resource_spend` | rubberduck gem 01; short physical token tick |
| Travel ticket gained / spent or discarded | `ticket_gain`, `ticket_spend` | rubberduck books 01 / 02; paper handling |
| Card gained / discarded; rumor card removed | `card_gain`, `card_discard` | rubberduck books 03 / 04; neutral paper handling, including harmful conditions |
| Gate opens / closes | `gate_open`, `gate_close` | Ogrebane teleport, forward / reversed |
| Regular / epic monster appears | `monster_spawn`, `epic_monster_spawn` | rubberduck monster 03 / lowered roar 03 |
| Monster defeated, including ambushes | `monster_defeat` | rubberduck creature death 01 |
| Mystery progress increases / mystery solved | `mystery_progress`, `mystery_solved` | PWL bell, single step / ascending layered resolution |
| Skill improvement gained / lost | `skill_gain`, `skill_loss` | PWL bell, ascending pair / lower tone |
| Rumor storm or vortex appears | `hazard_spawn` | rubberduck spell 02, lowered magical burst |
| Investigator defeated / devoured | `investigator_defeat`, `investigator_devoured` | PWL low dissonant bells / Ogrebane reversed teleport |
| Mythos phase begins | `mythos_phase` | PWL low bell warning |

Clue, focus, and ticket pickups also reuse the existing soft `token_land.wav`
when they reach the HUD. Health and sanity continue using their existing gain,
loss, departure, and landing cues.

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
(`justSpawnClue`, `justPlaceGate`, `justPlaceMonster`, `justSpawnStorm`,
`justAddVortex`, `justPlaceExpeditionToken`, `loadDefeatedInvestigator`) use silent
paths. All new cues, including queued ones, honor fast-forward at playback time.

No new sounds are attached to HUD refreshes, opening existing card panels,
decorative monster wandering, repeated highlights, or capped/no-op rest and
focus actions. These are not new rewards or threats. Rumor removal uses a neutral
paper cue because the same animation can follow success or failure.

## Verification

`GameplaySoundsTest` checks queued timing, fast-forward changes, failed preceding
events, missing audio, startup preloading, and every packaged WAV's format/size.
For an in-game listening pass: spawn clues and both monster types; collect a map
clue and a pool clue; gain/spend focus and tickets; open/close a gate; gain/discard
cards; improve/lose a skill; advance/solve a mystery; trigger a storm/vortex and
investigator defeat/devouring. Repeat during fast-forward, then reload a save
containing these map objects to check restoration stays quiet.
