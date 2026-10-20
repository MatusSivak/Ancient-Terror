# Bug report review — 20 October 2026

Fetched all pages of both collections from the local report viewer. The current
set contains three Android 1.7.17 bug reports and no crash reports. Attachments
are retained in ignored `build/report-review-oct20/`. Live statuses and saves
were not modified.

| Report | Resolution |
| --- | --- |
| `27128cdb-fd7e-4413-99d0-25e8d96c1a61` — silent Yig ending | The ending now uses a dedicated, non-looping Music instance instead of immediately playing a newly loaded Sound. This avoids Android's asynchronous SoundPool first-play race. Continue stops and disposes the instance. Both Ancient One endings share this fix. |
| `76f43420-9c28-440d-9cdd-b3909b426ea2` — missing solved-mystery text | Card questions move their label and buttons into the big-actor layer. Mystery positioning previously measured only prompts left in the small-actor/button layers. It now includes the moved controls, keeping the card above the gold text and OK button. |
| `e5bc4490-9ce2-450d-ba38-641dde01b16b` — glyphs in gold-on-black text | All LabelTable variants now use the shared glyph formatter. Gold prose is colored through markup with a white base style so the embedded icons retain their artwork colors. |

The audio race is a likely explanation for the device report, not a reproduced
device diagnosis. Android reports load completion separately through
[SoundPool.OnLoadCompleteListener](https://developer.android.com/reference/android/media/SoundPool.OnLoadCompleteListener).
The replacement follows the [libGDX Music lifecycle](https://libgdx.com/wiki/audio/streaming-music).

## Nun / Blessed reckoning

The existing rule implementation is correct for the reported case. Nun adds one
to Boon/Bane dice **before they are displayed and resolved**, capped at six:

- Raw 1 → displayed 2 → discard Blessed.
- Raw 2 → displayed 3 → keep Blessed.
- Raw 3–6 → displayed 4–6 → keep Blessed.

Added queue-level tests using the actual Blessed listener, owner-switch action,
TestService and RollAndShowDieAction. They start with a different active
investigator and verify owner switching, displayed values, result feedback and
condition discard. A Priest control case still discards on raw 2. No Nun
gameplay change was needed.

## Validation

- 308 service, action and view tests passed; zero failures, errors or skips.
- Desktop compilation passed.
- Production previews passed in English/Slovak settings and normal/fast-forward
  modes: solved prompt/card/button separation, viewport bounds, acknowledgement
  exactly once, and prompt glyph rendering without tinting icon artwork.
- Both Ancient One ending previews passed layout, background-click blocking,
  Continue, one playback, stop and resource disposal checks using an audio double.
- English/Slovak solved prompts and English glyph screenshots were visually
  inspected. Existing untranslated Crown prose remains English in Slovak mode.
- `git diff --check` passed.

Logs: `build/oct20-compile.log`, `build/oct20-validation.log`.
Screenshots: `build/report-fixes-preview/oct20-*`, `build/ending-preview/`.
Android device playback was not tested.

```powershell
.\gradlew.bat :eldritch-horror-core:service:service-impl:test :eldritch-horror-core:action:action-impl:test :eldritch-horror-core:view:view-impl:test -I tools/report-fixes-preview/preview.gradle -I tools/ending-preview/preview.gradle :desktop:previewReportFixes --args=--oct20 :desktop:previewAzathothEnding --offline --console=plain
```
