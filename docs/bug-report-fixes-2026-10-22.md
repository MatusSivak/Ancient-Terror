# Bug report review — 22 October 2026

Read every page of both collections from the local viewer: five bug reports,
zero crash reports. Attachments are in ignored `build/report-review-oct22/`.
Live report statuses and original saves are unchanged.

## Progress

- [x] `24ff4903` — verified the existing short No/Yes research improvement choice.
- [x] `48c5b066` — Priest explains his gate-triggered Boon and waits for OK before granting it.
- [x] `b2bf6c45` — Poisoned artwork now shows an investigator bitten by a snake.
- [x] `c2d47867` — reserve affordability shading survives temporary drag/zoom tint resets.
- [x] `f3a5c10b` — reroll questions show one Clue/Focus token, with space before `?`.
- [x] Tests passed and production widget previews inspected.

## Implementation and verification

The research change is already present in `0cbb0e6`. Existing encounter tests
verify short No/Yes choices, the single-skill payment, declining payment, and
the no-improvement-token case. No new research code was needed.

Priest uses a localized portrait notice. A deferred-response test confirms no
Boon is granted before OK. His existing eligibility restrictions remain tested.

Reserve affordability now has its own persistent black overlay, independent
of the temporary foreground tint used by drag/reset/zoom. The preview uses the
reported reserve (Holy Cross, Handcuffs, Spirit Dagger, Lucky Cigarette Case)
and a budget of one. All four are dimmed and have disabled drag sources;
clearing temporary tints and opening/closing the fourth card preserves this.
Taking Debt enables the four cost-two cards at budget three. Selecting one
dims the remaining three; returning it restores them.

Reroll prompts omit the separate token image when the text already contains
the glyph. Custom portraits and other images are preserved; custom questions
without an inline token keep their separate token image. English and Slovak
default prompts use one glyph and a space before the question mark.

Validation:

- View, event-listener, and service test suites: **384 tests, zero failures/errors**.
- Desktop Java compilation passed.
- Production Scene2D preview: `:desktop:previewReportFixes --args=--oct22`
  with `-I tools/report-fixes-preview/preview.gradle`; passed.
- Inspected English/Slovak Priest and reroll screenshots, reserve screenshots
  before/after Debt and selection, and the Poisoned card at its rendered size.
- Logs: ignored `build/report-review-oct22/tests.log` and `preview.log`.
- Screenshots: ignored `build/report-fixes-preview/oct22-*.png`.

These checks exercise the relevant production components with report-derived
fixtures, rather than replaying an entire saved game.

## Poisoned image generation

Used the built-in ImageGen tool with the existing Poisoned image as a style
reference. Final game asset: `assets/card/condition/POISONED.jpg`, 700 × 340 JPEG.
The generated original remains in the tool's generated-images folder. Resizing
and JPEG encoding preserve the game's card layout.

Final prompt:

> Use case: historical-scene. Asset type: replacement artwork for the Poisoned
> condition card in the 1920s pulp-horror game Ancient Terror. Input image is the
> existing card art, used as style and investigator reference. Create a new wide
> landscape image, approximately 2.06:1 aspect ratio, matching its realistic
> painterly cinematic style, restrained colors and period clothing. Show an
> adult 1920s male investigator outdoors at a jungle ruin, visibly ill and
> sweating after a venomous snake bite. He is seated on a stone step, sleeve
> rolled up, looking anxiously at two small puncture marks on his exposed forearm
> with mild redness, while a clearly recognizable snake retreats across the
> nearby stones. Make the bitten arm, distressed face and snake legible even at
> card-thumbnail size. The visual must communicate snakebite poisoning, not
> drinking poison or stomach illness. Non-graphic injury; no gore. Preserve
> period plausibility and the serious atmospheric style. Full edge-to-edge
> artwork only, no text, typography, card borders, logos, UI, or watermark.
