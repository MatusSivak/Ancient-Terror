# Changes and reports reviewed on 30 October 2026

Reviewed two active bug reports and one active crash report. Attachments are
retained in ignored `build/report-review-oct30/`. Remote main was verified as
`9ec534f`, matching the starting local commit. Fetch itself encountered a broken
local Codex checkpoint reference; no checkpoint refs were deleted or rewritten.

## Requested presentation changes

- Explorer setup explains the starting skill improvement with a portrait and OK
  acknowledgement before skill selection, following the Actress flow. It runs
  once for a new investigator and does not repeat when loading a save.
- Exhausting a card plays a short closing/latch sound with the disabled overlay.
- Monster defeat plays a flying sweep at takeoff and an explosion when the
  enlarged monster breaks apart. Both honor fast-forward at playback time.
- Replaced artifact opening/reveal and walk/rail/sea cues. Rail uses a downloaded
  train whistle; sea uses a downloaded low ship horn layered with waves. Sources,
  licenses and rebuild instructions are in `assets/sounds/OCT30_AUDIO.md`.
- Monster-sheet health departures are 30 ms apart, including during fast-forward.
  Each token starts at its rendered icon bounds, accounting for parent scale,
  and retains its left-to-right order at the destination. Combat hit departures
  use the same corrected destination ordering.

## Crash and encounter fixes

- `2bb1dc8d-beba-4aa0-b6e9-7c5aef5cf457`: Mi-Go Brain Case declared
  `KhopeshOfTheAbyssArtifact` as its listener type. Building its action menu cast
  the Brain Case to that unrelated type. Corrected the type and covered both
  acquisition and reload paths. A registry-wide test checks all artifact listener
  generic types against their actual cards.
- `5f9bcbc1-dab6-4fbe-87d1-e6d1d5fd2ca3` and
  `5feff725-0ef7-4648-a7a2-8f8095f4c819`: reviewed the Serpent People outcomes.
  City research 16 has separate results: zero Will successes causes Cursed;
  defeating the monster grants the Clue. Handcuffs does not undo the earlier
  Will result. The outcome text now explains the Will trigger in English and
  Slovak.
- Found and fixed the related victory-detection bug: `MonsterCup.defeatMonster`
  restores a monster's health while marking it no longer alive. Yig research
  and the K'n-yan guardian outcome checked only health and therefore treated
  actual defeats as surviving monsters. They now use `CombatData.isMonsterDefeated()`.
  Tests resolve encounters after the real cup defeat/reset, preserving the
  independent Will/Strength/health-loss conditions and withholding rewards for
  living survivors.

## Validation

- Full JVM suite: 573 tests passed; desktop compilation passed.
- `previewVitalityLoss`: passed counts, 30 ms departures, scaled source positions,
  destination order, normal/fast-forward completion, duplicate impacts, healing,
  overkill and cleanup. Inspected `four-monster-hearts.png`.
- `previewMonsterDefeat`: passed regular/epic monsters, ambushes, sizing,
  fast-forward, speed changes, callbacks and GPU cleanup. Instrumented sound
  playback verifies one takeoff cue, one explosion at shattering, no early
  explosion, and suppression when fast-forward starts before shattering.
- Packaged cue tests verify preloading, mono 44.1 kHz PCM16 and decoded size.
- Android device listening and APK packaging were not performed locally.

The pre-existing edit to `assets/save.json` is excluded from the commit.
