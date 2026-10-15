# Report fixes preview

From the repository root, using the JDK setup in `BUILDING.md`:

```powershell
.\gradlew.bat -I tools/report-fixes-preview/preview.gradle :desktop:previewReportFixes --args="--latest" --console=plain
```

The offscreen desktop preview renders the production Hall of Fame, Card Collection,
crash dialog (English and Slovak), and Crown of the Serpent card. It checks the
reported Yig clock, legacy records with unknown survival, skull placement,
repeated Back navigation without a checked state, crash acknowledgement exactly
once, dialog bounds, and clue costs of 2, 1, and 0.

It uses local fixture records and a disabled network client for Hall of Fame;
it does not submit reports, read game saves, or change language preferences.
Screenshots are written to `build/report-fixes-preview/`.

Omit `--latest` for the earlier skill, investigator, ticket, mystery glyph and
typewriter checks.

Use `--args="--oct15"` for the 15 October reports: all five rumor cards in English
and Slovak, text bounds, objective/failure/reckoning highlights, countdown
acknowledgement, defeated-investigator head portraits, and Reckoning glyphs at
normal and fast-forward speed. Screenshots use the `oct15-*` and
`typewriter-glyphs*` prefixes.
