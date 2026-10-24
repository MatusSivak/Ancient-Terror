# Button glyph colors — 24 October 2026

Follow-up to the latest report fixes: Clue glyphs could still appear almost black
in shared buttons. The label actor and label style multiplied the icon's colors
by the dark prose color, even when the icon had white color markup.

Shared glyph buttons now use neutral actor/style colors and apply the prose
color through markup. Typewriter choices also preserve each color run when
wrapping, keeping Clue green, Health red, and Sanity blue. Color changes no longer
create unintended line breaks.

Validation:

- The new rendered preview reproduced the dark Clue before the fix.
- View suite: 284 tests, zero failures or errors.
- `:desktop:previewReportFixes --args=--button-glyph-colors` checks actual green
  pixels, neutral glyph tints, short/wrapped/multiline choices, and choice results.
  It also checks all production Glimmer of Hope and The World Fights Back options.
- Preview uses `-I tools/report-fixes-preview/preview.gradle`. Screenshots are
  in ignored `build/report-fixes-preview/button-glyph-colors-*.png`; logs are
  in `build/report-review-oct23-evening/glyph-colors-*.log`.

Checked desktop production rendering; no Android APK was installed.
