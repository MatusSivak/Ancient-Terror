# Report viewer, portraits, and pending reports — 9 October 2026

The local Bug & Crash Reports viewer now supports **Report title** / **Save title**,
including Enter to save. Titles are searchable and persist in both collections.
Clearing a title restores the original display. A title-only Firestore update mask
preserves descriptions, stack traces, attachments, and status; the existing
update-time precondition prevents overwriting a concurrent edit.

The Butler, Priest, Nun, and Explorer portraits now use the exact alpha channel
shared by all 20 older investigators. Their RGB pixels and dimensions are unchanged.
`tools/apply-investigator-mask.cjs` reproduces the asset correction with Node and sharp.

## Pending report fixes

| Report | Change |
| --- | --- |
| `01584747-469c-46d9-86a0-d37d9c368cd9` | Questions whose choices are skills now use the existing skill table with glyphs, base values, bonuses, and only the eligible skills selectable. This covers Yig's two distinct improvement rewards and optional improvement-token spending. Spending a token remains possible at +2, and declining preserves the Detained outcome. |
| `2855df7b-535c-4174-9c28-7ca423994b24` | Mystery rule text now passes through the shared glyph formatter, including Observation and Will tests and their modifiers. |
| `2d86098d-ee83-4446-b8b0-169a7fb83e3d` | Questions whose choices are investigators now use the standard portrait selection dialog. This covers defeated-investigator selection, Sister Mary's Madness removal, and Father Mateo's Boon recipient. Each caller's candidate list is preserved, including defeated investigators, and optional Done responses still return null. |
| `839a04fa-7beb-42da-b0c4-673fe8e9a453` | The user confirmed that the missing choice was while buying. Buying now always opens the ticket picker and waits for a click, including cities with only one connection type. Types without a city connection are disabled and dimmed. Both buttons close after selection, and duplicate clicks cannot select again. City connection rules remain in effect. |
| `fb2b869c-e0f5-4936-aecf-2599619b87a6` | Zanthu Tablets is permanently included among default unlocked artifacts. Existing profiles and imported unlock lists merge the defaults and persist them, preserving previously earned unlocks. Normal deck availability rules still apply when an artifact is already held or discarded. |

These are local source fixes, not an Android release. Live report titles and
statuses were not changed as part of verification.

## Validation

- Viewer: 22 Python integration tests and 7 offline Chrome tests passed.
- Java: 246 view tests, 63 event-listener tests, 1 controller test, and 2 artifact
  repository tests passed. Desktop compilation passed.
- Production portrait preview rendered successfully.
- Production selection preview exercised eligible skills (including spending at
  +2), excluded skills, optional cancellation, investigator cancellation, ticket
  availability, duplicate-click prevention, and actual mystery glyph rendering.
- `git diff --check` passed.
- Android/device gameplay was not tested.

To reproduce the UI checks with the configured JDK:

```powershell
.\gradlew.bat -I tools/report-fixes-preview/preview.gradle :desktop:previewReportFixes --offline --console=plain
```

Screenshots are saved under `build/report-fixes-preview/`. The harness neither
loads a saved game nor accesses Firestore.
