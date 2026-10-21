# Card resolution and doom feedback — 21 October 2026

## Requested work

- [x] Add sound when doom retreats.
- [x] Replace the flip-card glyph with a symbol that indicates upcoming typewriter text.
- [x] Use the originating card artwork as the encounter background while resolving any flipped card.
- [x] Render success values such as 4, 5, and 6 as dice glyphs, including bracketed values.
- [x] Run focused tests and inspect production previews.

## Existing work to preserve

The preceding Nun change is implemented and uncommitted: original dice remain
visible until OK confirms the +1 explanation. Seven Nun tests and the English/
Slovak production preview passed at both animation speeds.

## Findings and validation

- Doom retreat now has an original synthesized WAV and regeneration script.
  The clock uses direction-specific cues, silent on setup/unchanged/fast-forward.
- Flip wording maps to a typewriter-and-paper glyph, with editable SVG source.
- Both shared spell/condition back listeners request card resolution paper.
  The artwork belongs to that paper and sits behind the HUD/text. Hiding the
  paper removes its backdrop; resuming restores it; finishing removes it.
  Nested card papers retain the underlying scene without changing the saved
  encounter background stack. These are all implemented flippable card types.
- Dice formatter now recognizes success-threshold lists, apostrophe-s forms,
  and explicit [1]–[6], preserving quantities and color markup.
- Validation complete: 392 view/listener/service/action tests passed, with zero
  failures, errors or skips. Desktop compilation and `git diff --check` passed.
- Production preview passed at normal and fast-forward speed: correct retreat/
  advance audio triggers; setup/unchanged/fast-forward silence; artwork exists
  for every spell and condition; nested backgrounds restore their parent;
  hide/resume/finish cleanup; flip and success glyphs present. Sound triggering
  used a test double; device audio playback was not tested.
- Inspected the spell and condition screenshots, including fast-forward. The
  new typewriter glyph, success dice and originating card artwork are visible.
- Existing Nun edits preserved. Nothing committed or published.

## Reproduction

Use the local JDK and Gradle cache documented in the conversation:

```powershell
$env:JAVA_HOME = 'C:\Users\Krtko\.jdks\corretto-22.0.2'
$env:GRADLE_USER_HOME = 'C:\Users\Krtko\.gradle'
.\gradlew.bat :eldritch-horror-core:view:view-impl:test :eldritch-horror-core:event-listener:event-listener-impl:test :eldritch-horror-core:service:service-impl:test :eldritch-horror-core:action:action-impl:test -I tools/report-fixes-preview/preview.gradle :desktop:previewCardResolution --offline --console=plain
```

Logs: `build/card-resolution-validation.log`,
`build/card-resolution-service-validation.log`, `build/card-resolution-preview.log`.
Screenshots: `build/report-fixes-preview/card-resolution-*.png`.

Remaining work: none for the four requested changes.

## Asset/artifact coverage follow-up

The initial artwork resolver only accepted spells and conditions. It now accepts
assets and artifacts as well, using the same artwork paths as their card views.
Inspection found no asset/artifact flip or typewriter resolution call sites;
their current effects use card prompts. Those existing flows are unchanged.
The shared card-resolution screen can now render all four card categories.

Validation passed: extended production preview checks every asset/artifact
artwork path and renders Holy Water and Necronomicon papers with hide/resume/
finish checks at both speeds. Screenshots inspected. Log:
`build/card-resolution-all-cards-preview.log`.
