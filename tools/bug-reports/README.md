# Bug reports

The game sends authenticated Firestore documents to `bugReports/{uuid}` in
`ancient-terror-hall-of-fame`, the same Firebase project as the existing high score
Realtime Database. No Cloud Storage or new platform SDK is required.

## Required console setup

1. In that project's Firebase console, create the **default Cloud Firestore database** if absent.
2. In Authentication > Sign-in method, enable **Anonymous** sign-in.
3. Merge the `bugReports` block from `firestore.rules` into the deployed rules.
   Preserve existing collection rules; ensure no broader rule grants public reads.
4. `assets/bug-report-firebase.json` contains public Firebase client configuration
   copied from the repository's `google-services.json`. If its API key is restricted
   to Android, supply a Firebase web client key usable by the REST clients on the
   supported platforms. Restrict API access to Identity Toolkit and Firestore.
   Keep `appVersion` aligned with Android's `versionName` when releasing.
5. Test one report on desktop and Android, verifying its Firestore document and
   both attachments. No live report or cloud configuration was submitted by this change.

## Payload

- Description (up to 4,000 characters), anonymous reporter UID, schema version and status.
- Client capture time in milliseconds; Firestore also provides authoritative `createTime`.
- Latest existing local `save.json` as **bytes** (`saveFile`), never parsed, overwritten,
  or truncated. Missing saves are recorded in metadata; saves above 512 KiB block
  submission with an explanatory message.
- Optional PNG screenshot as **bytes** (`screenshotPng`), captured before the menu
  opens and reduced to at most 200 KiB. It is the game framebuffer only. Capture
  failure does not prevent reporting; the UI and metadata indicate its absence.
- App version, platform and platform version, language, framebuffer dimensions,
  density, attachment status and save modification time. No hardware identifiers,
  account email, filesystem paths or other applications' screenshots are collected.

The combined attachment limits leave room under Firestore's 1 MiB document limit.
Bytes are base64 in the REST representation. Decode `saveFile.bytesValue` to recover
the original save and `screenshotPng.bytesValue` to recover the PNG. Exempt these two
fields from indexing in the Firestore console. Reports are readable only through
trusted project administration; clients can only create them.

Retry keeps the same UUID, attachments and description while the dialog remains
open. This avoids duplicate documents after a lost response. The dialog explicitly
shows success only after a successful create or an already-existing document response.
Closing an unsuccessful report discards the draft; there is no offline upload queue.
Transport uses 20-second timeouts and displays failure without closing the dialog.

Anonymous auth and field validation are a baseline, not abuse throttling. For a public
launch, add an App Check/rate-limited ingestion endpoint if submission abuse occurs.

References: [Firestore REST authentication](https://firebase.google.com/docs/firestore/use-rest-api),
[anonymous sign-in](https://firebase.google.com/docs/reference/rest/auth),
[Firestore values and limits](https://firebase.google.com/docs/firestore/reference/rest/v1/Value).
