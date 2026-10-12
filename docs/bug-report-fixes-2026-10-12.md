# Bug report review — 12 October 2026

Fetched all current Firebase report pages and downloaded the five bug reports and
their attachments. There are currently no crash reports. The five reports were
created on 8 October; the glyph report's title was updated on 12 October to include
Mythos typewriter text. All five still have status `new` in Firebase.

| Report | Review outcome |
| --- | --- |
| `2855df7b-535c-4174-9c28-7ca423994b24` | Fixed the newly requested Mythos typewriter coverage. Rule text now uses skill, resource, and dice-result glyphs through the shared formatter. Modifiers, numeric quantities, and good/bad outcome colors are preserved. Icons retain their artwork colors. Keyword matching avoids replacing fragments of names such as William. Flavor prose remains unchanged. The earlier mystery-card fix was verified in the production preview. |
| `01584747-469c-46d9-86a0-d37d9c368cd9` | Existing typed skill questions already route through the skill table. Rechecked eligible skills, improvement-token spending at +2, and cancellation in the production preview. |
| `2d86098d-ee83-4446-b8b0-169a7fb83e3d` | Existing investigator questions already route through the portrait selector. Rechecked the production dialog and its optional Done response. |
| `839a04fa-7beb-42da-b0c4-673fe8e9a453` | Existing ticket buying waits for an explicit selection, even with one available connection type. Rechecked disabled choices and duplicate-click prevention. |
| `fb2b869c-e0f5-4936-aecf-2599619b87a6` | Zanthu Tablets is already a permanent default unlock. The repository tests confirm persistence and merging with existing/imported unlocks. Normal artifact availability rules still apply. |

## Validation

- View: 249 tests; event listeners: 63; controller: 1; model: 16; artifact unlocks: 2. All passed (331 total, including Gradle up-to-date suites).
- Desktop Java compilation passed.
- Production UI preview passed skill/investigator choices, ticket availability,
  mystery glyphs, and typewriter glyph rendering with exactly one completion at
  normal and fast-forward speed. Rendered screenshots were visually inspected.
- `git diff --check` passed.

To reproduce the production preview:

```powershell
.\gradlew.bat -I tools/report-fixes-preview/preview.gradle :desktop:previewReportFixes --offline --console=plain
```

Screenshots are under `build/report-fixes-preview/`, including
`typewriter-glyphs.png` and `typewriter-glyphs-fast.png`. Downloaded report data is
kept in ignored `build/report-review-oct12/`.

These are local source fixes. No release was built, live report statuses were not
changed, and Android gameplay was not tested. The configured Android SDK directory
`C:\WORK\android_sdk` is absent.
