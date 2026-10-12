# Awakened Yig — 12 October 2026

Awakening now sets Doom to **−8**, displayed by the clock as **23:20**.
Doom advances toward 0, clamping at midnight and ending the game. Retreating Doom
moves it farther from 0. The awakened sheet no longer displays Eldritch tokens.
Old awakened saves with remaining power tokens convert that count to negative
Doom; saves using the new countdown retain their saved time.

When Doom would retreat and a Cursed condition is available, the player chooses
**Retreat Doom** or **Discard Cursed**. The latter removes one Cursed condition
instead of changing Doom, using the existing portrait picker when several targets
are eligible. Without a Cursed condition, Doom retreats normally.

Awakened Reckoning spawns exactly one Cultist at a random board location without a
location picker. Sleeping Yig retains its expedition Reckoning. Investigator
replacement and the existing three-Mysteries-then-Yig final battle remain intact.

Sheet text:

- Flavor: “The temple stones split beneath his rising coils. Across the world,
  serpents answer their father's call. Yig has come to claim the blood of those
  who slew his children. His vengeance will spare no one.”
- Special rules: “Whenever Doom would retreat, you may discard one Cursed Condition instead.”
- Reckoning: “Spawn one Cultist at a random space.”
- Victory: “Solve three Mysteries, then defeat Yig to win.”

## Artwork

Created with the built-in image generation tool, using the existing sleeping Yig
art as a character/style reference. The new 2172 × 724 RGBA asset preserves its
generated transparency and is selected only for awakened Yig:
`assets/ancient_one/yig-awakened-sheet-art.png`.

Final generation prompt:

> Use case: stylized-concept. Create a new dedicated AWAKENED YIG illustration for a dark fantasy board-game Ancient One sheet. The supplied reference is only a character/style reference, not an edit target: preserve recognition of Yig, a colossal dark olive and bronze serpent god with a feathered ancient gold ceremonial crown, glowing amber eyes, clawed arms and coiled snake body. Show his awakened form in a forceful threatening pose, head raised, fangs exposed, claws spread, immense coils breaking out of ruined jungle temples, smaller serpents and drifting dust at his base. Detailed painterly realism matching the reference's antique horror illustration. Wide horizontal banner, roughly 3:1 composition, central silhouette and face readable at 696x220 display size. Keep the complete crown and claws inside the canvas. Isolated artwork with genuinely transparent surrounding background and irregular natural edges fading through sparse mist, suitable over parchment. No text, no letters, no title, no borders, no UI, no counters. Dramatic amber light with deep moss green and antique bronze; distinct more fierce awakened pose than the reference.

## Validation

344 tests passed across action, event-listener, model, controller, and view modules.
Coverage includes awakening, multi-step Doom advances, midnight, retreats, both
curse choices, no eligible curses, random Cultist spawning, save conversion,
negative-Doom persistence, and clock display mapping. Desktop compilation passed.

The production Yig preview renders the new artwork and verifies exact rules text
with no token counter or investigator text. Screenshot:
`build/yig-preview/yig-awake.png`.

The production clock preview also passed at normal and fast-forward speed:
awakening resets to 23:20, advances and retreats animate in the correct direction,
and resetting the clock does not repeat a completed action's callback.
`git diff --check` passed.

```powershell
.\gradlew.bat -I tools/yig-preview/preview.gradle :desktop:previewYig --offline --console=plain
```

No Android release or device test was performed; the configured SDK is absent.
