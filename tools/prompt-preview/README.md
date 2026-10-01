# Mystery prompt and dice regression preview

Run from the repository root with the JDK configured as described in `BUILDING.md`:

```powershell
.\gradlew.bat -I tools/prompt-preview/preview.gradle :desktop:previewPrompts
```

Uses an offscreen desktop window and silent audio stubs without loading a saved game.
Checks mystery spacing with a standalone button and a label, recentering after prompt
removal, partial reroll completion, visible dice during Focus/Clue prompts, and failed-roll cleanup
at normal and fast-forward speed. Screenshots are written to `build/prompt-preview`.
Also renders the top combat horror/damage icons with ordinary and large token counts.
Includes `combat-panel-*` previews of the monster status plate (name, horror, damage and
health above the roaming monster) for both active phases, long names, eight tokens per
row, and updated/zero stats.

Renders the pre-test asset selection with 1–3 cards, in combat and outside combat, and
fails if the cards panel, test summary or Test button overlap each other or the fixed HUD
controls (side menu, clock, menu/fast-forward buttons, investigator HUD). It also checks
that the monster portrait wanders (creature layer, drawn under the cards panel and dice, inside `getWanderArea()`), and renders
the combat overview and a test result table. monster-health-* checks that the status
plate rides above the roaming monster and that health fireballs stop it and land on its
body. `monster-horror-shootdown-*` show dice fireballs burning the monster's horror icons on its
plate first; `monster-attack-*` show the remaining sanity (blue) and health fireballs
hitting the investigator. Plate tokens must stay in their slots throughout. A further
check drives `TestViewImpl.destroySanity/destroyHealth` with the stand wrapped three times
across the map and requires the fireballs to land on the on-screen copy, thrown one after\r
another, with exactly one sanity/health token lost per impact. `combat-interrupted` /
`combat-resumed` check that an interruption (Flesh Ward, another investigator stepping in)
hides the monster, puts torn tokens back, and that the next combat step brings it back.
`epic-monster-*` show an epic monster (bigger portrait, gold rim and plate, bigger fireballs)
and the Horror → Damage phase pill in the top-left corner; `combat-overview-epic` shows the
gold epic card on the overview. `map-monster-walk-*` show a map monster stepping while it
moves (body and shadow bounce and waddle together; its real position stays put).

Exercises the actual combat result and claw-destruction sequence with zero and one
success at normal and fast-forward speed. Checks that dice slide fully below the
screen and that zero-success cleanup removes the finished roll. A comparison case
recreates the old concurrent move-up/slide-down ordering and verifies that it
reproduces the lingering dice.
