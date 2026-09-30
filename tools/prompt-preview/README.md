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
Includes close-up combat-panel previews for both active phases, long names, eight
tokens per row, and updated/zero stats.

Exercises the actual combat result and claw-destruction sequence with zero and one
success at normal and fast-forward speed. Checks that dice slide fully below the
screen and that zero-success cleanup removes the finished roll. A comparison case
recreates the old concurrent move-up/slide-down ordering and verifies that it
reproduces the lingering dice.
