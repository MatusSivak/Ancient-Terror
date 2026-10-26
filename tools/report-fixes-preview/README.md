# Report fixes preview

Use `--args="--button-glyph-colors"` to check shared and typewriter button glyph
colors, including actual green Clue pixels, short and wrapped choices, explicit
line breaks, and the two evening mythos prompts. Screenshots use
`button-glyph-colors-*` and `oct23-evening-*`.

Use `--args="--oct23-evening"` for production Glimmer of Hope and The World
Fights Back choices. Checks colored glyphs, compact first-use sizing, wrapping,
button bounds, every option's result, and restoring the question area.
Screenshots use `oct23-evening-*`.

Use `--args="--oct23"` for single-line Priest and insufficient-Clue notices in
English and Slovak, plus grouped Cultist movement. The production movement check
verifies synchronized token positions, one camera journey, world wrapping,
normal/fast-forward speed, and that unrelated monsters stay put. Screenshots
use `oct23-*`.

Use task `:desktop:previewCardResolution` with the same init script to check
spell/condition/asset/artifact artwork behind typewriter paper, nested background restoration,
hide/resume/finish cleanup, flip and success dice glyphs, and doom sound direction.
It verifies all four card categories' artwork paths and captures `card-resolution-*`
screenshots at both animation speeds. Audio playback uses a recording double.
It also verifies that Debt artwork stays opaque through paper hiding, delayed
dice setup, result handoff, and resumed paper at both speeds, with final cleanup.
Debt screenshots use `oct23-evening-debt-*`.

Use `--args="--nun"` for Nun's visible Boon/Bane bonus: raw dice settle, a localized
+1 notice waits for OK, then faces and success colors update before completion.
Checks 2/4/6 → 3/5/6 in both languages and at both animation speeds, including a
10-second wait without confirmation and double-click protection. Screenshots
use `nun-bonus-*`.

Use `--args="--oct20"` for the 20 October review: the solved-mystery gold prompt
stays above OK and below the Crown card after moving to the card layer. Also
checks glyphs in gold-on-black prompts, preserving icon colors, in both language
settings and at both animation speeds. Screenshots use `oct20-*`.

Use `--args="--crown"` for the later 19 October reports: production mystery-card
acknowledgement, camera focus on the Crown holder, refreshing an empty HUD to the
holder's Clue/Focus counts, and complete token-loss animations in both language
settings and at normal/fast-forward speed. Screenshots use `oct19-crown-*`.

Use `--args="--oct19"` with the command below for queued camera moves at normal
and fast-forward speed (including reentrant, wrapped and zero-duration moves),
ticket glyphs in typewriter rules, and English/Slovak card and investigator
ability text. Screenshots use `oct19-ticket-rules-*` and `typewriter-glyphs*`.

From the repository root, using the JDK setup in `BUILDING.md`:

Use `--args="--oct25"` for condition cards shown before accepting Debt, choosing
among conditions, ambush defeat size, investigator-token removal during exit,
and Slovak mystery glyphs. Checks normal and fast-forward animation. Grouped
movement also covers Buenos Aires–Himalayas in both directions and asserts the
camera and monsters travel the same way. Screenshots use `oct25-*`,
`oct23-cultists-route*`, and `mystery-glyphs.png`.

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

Use `--args="--awakening"` for Yig's midnight and third-mystery confirmations,
OK acknowledgement, all four awakened Ancient One sheets, and the 23:20 clock
reset from several starting times at normal and fast-forward speed. It checks
English and Slovak text, button separation, and sheet text bounds. Screenshots
use the `awakening-*` and `awakened-*` prefixes.

Use task `:desktop:previewMonsterArtwork` with the same init script for the custom
Yig Cultist and updated Yig epic sprite. It checks Ancient One switching/fallback,
matching synchronous/asynchronous textures, and production cards, selections,
map tokens, combat sprites, and combat overviews. Screenshots use `cultist-yig-*`
and `yig-epic-*` prefixes.

Use `--args="--yig-clue"` for spending a Clue to advance Yig's Active Mystery.
It finishes a hidden research paper, answers the plain Yes/No question and a
K'n-yan paper choice, then requires the discarded Clue token to stay visible
instead of fading out with the small-actor layer. Screenshots use the
`yig-clue-*` prefix.
