# Bug report fixes — 24 October 2026

Reviewed the latest report and its screenshot, log, and save from the local
viewer. Original attachments remain in ignored `build/report-review-oct24/`.
The live report is marked **Fixed**.

## Changes

- `a7ed8d48-c4cd-4500-b5ea-c68714baa870` — Righteous's "Daring Rescue" back
  says the chosen Monster loses five Health, but it dealt only three. It now
  deals five, matching the card text.

## Verification

- New `RighteousDaringRescueTest` resolves the back with production listener
  code and checks that the selected Monster receives five damage. Existing
  `RighteousRewardTest` still passes.
