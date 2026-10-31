# Spawn, card-category and monster audio

Prepared with `python tools/prepare_category_sounds.py` (`numpy` and `soundfile` required).
All sources are downloaded CC0 or public-domain recordings retained in
`source_assets/sounds/categories/`; processing is limited to trimming, layering recordings,
pitch shifting, DC-offset removal, mono mixdown, resampling and level normalization. Every clip
ends with a smooth cosine fade-out (longer for ringing bells, chimes and voices) instead of a cut. The script
supersedes `map_investigator_impact` from `prepare_landing_sounds.py`, `gate_resonance` and
`monster_prowl_1`–`4` from `prepare_presentation_sounds.py`, `clue_spawn`, `clue_gain`,
`focus_gain`, `ticket_gain`, `dice_sort`, `card_discard` and the `*_land` HUD arrivals from `prepare_gameplay_sounds.py` /
`prepare_tabletop_sounds.py`, `health_gain` from `generate_token_sounds.py`, `monster_defeat_fly` and
`monster_defeat` from `prepare_oct30_sounds.py`, and `investigator_detained` from
`generate_detained_sound.ps1`; run it after them.

## Downloaded sources

BigSoundBank / LaSonothèque, CC0 ([license](https://bigsoundbank.com/licenses.html)):
[Jumping on wooden pontoon #1](https://bigsoundbank.com/jumping-on-wooden-pontoon-1-s1841.html),
[Great page that turns #1](https://bigsoundbank.com/great-page-that-turns-1-s0362.html),
[Counter bell #3](https://bigsoundbank.com/counter-bell-3-s2531.html),
[Chimes "Dream" #8](https://bigsoundbank.com/chimes-dream-8-s2086.html),
[Church bell](https://bigsoundbank.com/church-bell-s0135.html),
[Delirious laughter #3](https://bigsoundbank.com/delirious-laughter-3-s2093.html),
[Punch #5](https://bigsoundbank.com/punch-5-s2460.html),
[Bones cracking #2](https://bigsoundbank.com/bones-cracking-2-s1408.html),
[Man cough #5](https://bigsoundbank.com/man-cough-5-s2183.html),
[Pencil, signature #2](https://bigsoundbank.com/pencil-signature-2-s3237.html),
[Thunder #2](https://bigsoundbank.com/thunder-2-s3113.html),
[Gong, strong #1](https://bigsoundbank.com/gong-strong-1-s1483.html).

[4 Space / Portal Sounds](https://opengameart.org/content/4-space-portal-sounds),
Écrivain, OpenGameArt, CC0 (`1.mp3` from `tts.zip`).

Freesound, CC0 ([license](https://creativecommons.org/publicdomain/zero/1.0/)); the
high-quality MP3 previews were downloaded and are retained as `<id>__<author>__<name>`:

| Sound | Author | Used for |
| --- | --- | --- |
| [Ahem 1](https://freesound.org/s/436107/) | DrFortyseven | Ally throat clearing |
| [Demon Chant (Latin)](https://freesound.org/s/146143/) | cycro | Incantation (recorded voice and whisper) |
| [ganze gompa monks](https://freesound.org/s/98845/) | djgriffin | Ritual (Tibetan monastery field recording; 8–14 s excerpt kept as lossless FLAC) |
| [Bear Angry Growl](https://freesound.org/s/763026/) | celldroid | Monster sheet (field recording) |
| [Tiger Roar](https://freesound.org/s/263115/) | lauramellis | Epic monster sheet (zoo recording) |
| [Feet Squelch](https://freesound.org/s/683520/) | eqavox | Monster movement |
| [Wet-Slimy-Squishy footsteps](https://freesound.org/s/834075/) | Federicoy | Monster movement (raw-chicken foley; 0–10 s excerpt kept as lossless FLAC) |
| [UIMvmt_Equip Item, Weapon or Armor, Metallic](https://freesound.org/s/829499/) | harrisonlace | Weapon (recorded nuts and bolts) |
| [Wooden Thud (Mono)](https://freesound.org/s/449955/) | Breviceps | Clue spawn (body) |
| [Moving piece on a boardgame](https://freesound.org/s/556387/) | zachrau | Clue spawn (token tap) |
| [Wood_Sliding_Various-Objects](https://freesound.org/s/403653/) | LamaMakesMusic | Dice dragged back into order (0–4 s excerpt kept as lossless FLAC) |
| [Pencil_scibble](https://freesound.org/s/85703/) | tmkappelt | Clue gained |
| [Aum in concrete pipe](https://freesound.org/s/696440/) | itinerantmonk108 | Focus gained (sung "aum", quiet with a long fade) |
| [Ripping Paper](https://freesound.org/s/272028/) | aldenroth2 | Card torn apart (asset discarded, rumor removed) |
| [Hole-punch / puncher](https://freesound.org/s/447927/) | Breviceps | Ticket gained |
| [Poker Chip Drop](https://freesound.org/s/540369/) | Za-Games | Health / sanity token reaches the HUD |
| [Piece Placement](https://freesound.org/s/546119/) | el_boss | Clue reaches the HUD (wooden chess piece) |
| [Puzzle piece placed on board](https://freesound.org/s/560698/) | el_boss | Focus reaches the HUD |
| [place_card](https://freesound.org/s/502658/) | eggdeng | Ticket reaches the HUD |
| [JailDoorClose](https://freesound.org/s/80449/) | TurtleLG | Investigator detained |
| [Swoosh](https://freesound.org/s/264777/) | Shinplaster | Defeated monster flies toward the screen |
| [Gore Impact - "LOT OF HEART"](https://freesound.org/s/641046/) | magnuswaker | Monster explodes (impact) |
| [goreSplat.wav](https://freesound.org/s/414296/) | ThefitzyG | Monster explodes (flesh burst) |
| [quick woosh](https://freesound.org/s/683101/) | florianreichelt | Doom / omen track zooms in |
| [Whoosh](https://freesound.org/s/60013/) | qubodup | Doom / omen track zooms out (bamboo swing) |
| [Heart Beating](https://freesound.org/s/273150/) | michorvath | Health restored |
| [relieved sigh](https://freesound.org/s/242909/) | Reitanna | Sanity restored |

## Cues

| Cue | Category / traits | Sound |
| --- | --- | --- |
| `map_investigator_impact` | Investigator spawn | Heavy landing thump on wood, pitched down |
| `gate_resonance` | Gate spawn | Swelling portal opening |
| `clue_spawn` | Clue placed on the map | Wooden token thud with a tap |
| `dice_sort` | Thrown dice dragged back into order | Quiet, gentle wooden drag (no rattle) |
| `clue_gain` | Clue gained | Quick pencil scribble (noting the clue) |
| `focus_gain` | Focus gained | Quiet sung "aum" fading out smoothly |
| `card_discard` | Card destroyed (plays as the card tears) | Paper ripped by hand |
| `ticket_gain` | Train or ship ticket gained | Ticket hole punch |
| `health_gain` / `sanity_gain` | Health / sanity restored | Heartbeat / relieved sigh |
| `token_land` | Health / sanity token reaches the HUD | Poker chip dropped on a table |
| `clue_land` | Clue reaches the HUD | Wooden piece snap with a soft thud |
| `focus_land` | Focus reaches the HUD | Cardboard puzzle piece placed |
| `ticket_land` | Ticket reaches the HUD | Card placed on a table |
| `investigator_detained` | Investigator detained | Cinematic jail door slam |
| `monster_defeat_fly` | Defeated monster flies toward the screen | Swelling flyby swoosh |
| `monster_defeat` | Monster bursts apart | Wet gore impact into a flesh splatter |
| `track_zoom_in` / `track_zoom_out` | Doom or omen track zooms in / out | Quick soft woosh / lowered bamboo swing |
| `monster_prowl_1`–`4` | Monster movement | Lowered mud squelch steps with a wet slimy layer |
| `monster_sheet_growl` | Monster clicked, sheet opens | Angry bear growl, slightly lowered |
| `epic_monster_sheet_roar` | Epic monster clicked, sheet opens | Tiger roar, pitched far down |
| `ui_card_reveal_spell_incantation` | Spell: Incantation | Chanted Latin with whisper |
| `ui_card_reveal_spell_ritual` | Spell: Ritual | Gong strike over monks' chant |
| `ui_card_reveal_spell` | Spell: Glamour (other) | Unchanged spell shimmer and book |
| `ui_card_reveal_asset_weapon` | Asset: Weapon | Metallic equip rattle (suits guns and blades) |
| `ui_card_reveal_asset_tome` | Asset: Tome | Grimoire page turn |
| `ui_card_reveal_asset_ally` | Asset: Ally | Polite throat clearing ("ahem") |
| `ui_card_reveal_asset_service` | Asset: Service, Task | Counter bell |
| `ui_card_reveal_asset_trinket` | Asset: Trinket, Magical, Relic | Chimes |
| `ui_card_reveal_asset` | Asset: Item (other) | Unchanged leather and latch |
| `ui_card_reveal_condition_boon` | Condition: Boon | Church bell |
| `ui_card_reveal_condition_madness` | Condition: Madness | Delirious laughter |
| `ui_card_reveal_condition_injury` | Condition: Injury | Punch with bone crack |
| `ui_card_reveal_condition_illness` | Condition: Illness | Cough |
| `ui_card_reveal_condition_deal` | Condition: Deal | Pencil signature |
| `ui_card_reveal_condition_bane` | Condition: Bane | Thunderclap |
| `ui_card_reveal_condition` | Condition: Restriction (other) | Unchanged chains |

Cards with several traits use the first match in table order (`UiSounds.spellCue`,
`assetCue`), so a magical weapon sounds like a weapon and an Incantation/Teamwork spell
like an incantation. The monster voice plays only when a click opens the sheet, not when it
closes it or when the game opens the sheet itself.
