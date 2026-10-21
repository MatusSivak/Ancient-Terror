# Bug report review — 21 October 2026

Fetched all pages of both collections from the local report viewer. Five new
Android 1.7.17 bug reports arrived after the 20 October batch, and there were no
crash reports. Attachments are kept in the ignored `build/report-review-oct21/`
directory. Live statuses and saves were not modified.

| Report | Resolution |
| --- | --- |
| `f3a5c10b` — "Reroll with [clue]?" spacing | The English reroll prompts now put a space between the glyph word and the question mark (`Reroll with Clue ?` / `Reroll with Focus ?`), so the icon is no longer glued to the "?". |
| `80327b5c` — "only [six die] counts as success" | Already fixed by the dice glyph formatter work from 21 October. The Cursed title goes through `MarkupText.replaceDiceResults` via the shared LabelTable formatter, and `MarkupTextTest` covers it. No change needed. |
| `24ff4903` — Yig research City 15 buttons | Paying with an improvement token is now a short No/Yes choice on the typewriter paper, consistent with the other Yig research cards. Yes spends a token (auto-selected when only one skill is improved, otherwise a stat picker without a cancel button) and cures a Curse. No, or having no tokens, gives Detained. |
| `922bbc04` — unexplained Cultist spawn | Before the Yig reckoning spawns a Cultist, a notice with Yig's portrait and an OK button now explains it. There are two versions: one for the active expedition (while Yig is asleep) and one for a random space (once Yig is awake). |
| `cac2ad82` — unexplained doom advance | When the active expedition has 2 or more Monsters after the spawn, a second Yig notice explains the doom advance before it is applied. |

New strings are in `ui.properties` and `ui_sk.properties`
(`ancientOne.yig.reckoning.*`).

## Validation

- `:eldritch-horror-core:event-listener:event-listener-impl:test`
- `:eldritch-horror-core:view:view-impl:test`
- `:eldritch-horror-core:service:service-impl:test`
- `:desktop:compileJava`

New or updated tests: `YigRulesTest`, which covers the reckoning notice order and
the doom notice, and `YigEncounterPresentationTest`, which covers the City 15
detain-only path and the No/Yes paper choice.
