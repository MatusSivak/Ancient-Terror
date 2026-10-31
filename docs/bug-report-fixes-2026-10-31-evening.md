# Bug report fixes — 31 October 2026, evening

Updated from `origin/main` before editing; the checkout was already current.
Refreshed both report collections. Three open reports required changes; report
`fc967378-66dc-449d-921e-3ae366c6795b` only supplies a Yig victory save.
Original reports and attachments are retained in ignored
`build/report-review-oct31-evening/`. The existing `assets/save.json` edit is
excluded from this change.
The local report viewer stopped responding before status updates could be sent,
so remote report statuses and attachments remain unchanged.

- `232332d5-c794-4c9d-9963-04fb6b4a9f36`: Serpent Crown discarded Conspiracy
  Theorist, but its already queued reckoning still ran. Unregistering a card now
  invalidates pending reckoning commands and clears its charge. Re-registering
  the listener cannot revive an old command. Ordinary condition reckonings use
  the same lifecycle, alongside assets, artifacts, Blessed, Cursed and Poisoned.
- `baea5079-f4de-46a1-bd48-8c63d5cfa208`: awakening Yig spawns one Cultist on
  space 7, his lair and the location of the final Serpent's Nest confrontation.
  Both English and Slovak awakening notices and midnight rules describe it.
  Loading an awakened game restores listeners without repeating the spawn.
- `f9889e10-b425-4f49-889c-ac542018b144`: unconditional card discards no longer
  play the card-opening sound or display an OK button. The card reveals, tears,
  and completes with its original OK/NOTHING response after the animation.
  The tear sound remains; YES/NO decisions still require player input.

## Verification

- All 603 JVM tests pass, with zero failures, errors or skipped tests.
- Desktop build passes.
- The card-destruction preview passes for all four card categories, hidden cards,
  normal/fast-forward playback, suppressed opening sounds, absent confirmation
  buttons, preserved responses and real choices, and animation resource cleanup.
  Its old card-gain assertion was updated to match the existing behavior: gain
  confirmation returns immediately while the flight continues.
- Android checks could not run: the configured local Android SDK does not exist.
