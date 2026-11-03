# Bug report fixes — 3 November 2026

Refreshed both report collections and checked the attached screenshots, logs and
saves. Fetched `origin/main`; the checkout was already current. Original reports
and attachments are retained in ignored `build/report-review-nov3/`.
The pre-existing deletion of `assets/save.json` is excluded from this change.

- `2d20bce8-8eac-42ef-b2bf-77195c8f294d`: K'n-yan's escape now uses the existing
  map location picker, restricted to adjacent spaces, instead of location-name
  buttons. Movement still precedes becoming Delayed. Empty and single-destination
  cases retain their automatic behavior, and duplicate destinations are removed.
- `3e0c6ada-f294-489b-b1fd-5faeb9f497ff`: the possessions page now uses a
  750-by-450 content area with smaller margins. Both rows fit without scaling down
  the whole sheet and its scroll viewports. Cards retain horizontal scrolling and
  the existing zoom/dismiss controls.
- `dc3f17a8-b937-4438-acca-5c711e4c75f6`: failed Yig wilderness research 18 now
  randomly picks among the nearest cities and moves there before gaining Detained,
  without a destination question. It never picks a more distant city.
- `232332d5-c794-4c9d-9963-04fb6b4a9f36`: the previous Conspiracy Theorist fix is
  already present. Its regression test still confirms that discarding the ally
  invalidates its queued reckoning, even if the listener is registered again.

All four reports are marked Fixed in the viewer; attachments are preserved.

## Verification

- All 670 JVM tests pass: zero failures, errors or skipped tests.
- Desktop build passes.
- An offscreen production-widget preview verifies that the possessions scroll
  panes stay inside the expanded page. Visually checked
  `build/report-review-nov3/possessions.png`.
- Android was not built locally: `local.properties` points to the absent
  `C:/WORK/android_sdk`.
