# Bug reports

## Local report viewer

If `gcloud` is not recognized on Windows, install Google Cloud CLI first:

```powershell
winget install --id Google.CloudSDK --exact --source winget
```

Open a new PowerShell window after installation so it picks up the updated PATH.

Run the browser tool from the repository root (Python 3.10+):

```powershell
python -m pip install -r tools/bug-reports/requirements.txt
gcloud auth application-default login
python tools/bug-reports/viewer.py
```

The Google Cloud CLI (`gcloud`) must be installed for that login command. Sign in
with a Google account that has Firestore IAM read permission on the project
(for example `roles/datastore.viewer`). The game's Firebase API key and anonymous
login cannot read reports. No Firestore security rule changes are needed.

Alternatively, use a service account credential file stored **outside this repository**:

```powershell
$env:GOOGLE_APPLICATION_CREDENTIALS = 'C:\private\firestore-viewer.json'
python tools/bug-reports/viewer.py
```

An existing Google OAuth access token can also be supplied through
`GOOGLE_OAUTH_ACCESS_TOKEN` (no Python dependencies required in this mode).
Tokens expire; replace the environment variable and restart when needed.
Application Default Credentials refresh automatically.

The browser opens automatically. Reopen using the full URL printed in the terminal
if needed; it includes a local session token. The server listens only on
`127.0.0.1`; Google credentials never go to the browser. Stop with Ctrl+C.
Options: `--project PROJECT_ID`, `--database DATABASE_ID`, `--port 8765`,
and `--no-browser`. Defaults target `ancient-terror-hall-of-fame` / `(default)`.

The viewer loads all report summary pages, then supports text search, status and
platform filters, and date sorting. Select a report for its description, full-size
screenshot, timestamps, reporter UID, all metadata, and attachment sizes. Download
the original save, PNG, or complete Firestore JSON including base64 attachments.
Unknown fields are available under **All fields**. Missing attachments, empty
collections, authentication errors, and partial fetch failures are shown explicitly.
Refresh fetches current reports; there is no background polling.

Use **Report status** and **Save status** to assign one of:
- **New**: received, awaiting review.
- **Investigating**: being reproduced or worked on.
- **Fixed**: a fix has been implemented.
- **Closed**: no further work needed (for example duplicate or not reproducible).

Status updates preserve every other field and attachment. **Delete report** asks
for confirmation, then permanently deletes the document including its screenshot
and save file. Download anything you want to keep first. Both operations reject
stale report versions; refresh before retrying if another editor changed a report.

Editing requires IAM document update/delete permissions (for example
`roles/datastore.user`); `roles/datastore.viewer` allows browsing only. The tool
shows an access error if the signed-in account lacks permission. Game-client
Firestore rules stay unchanged; these administration requests use Google IAM.
Summary reads exclude attachment bytes; opening reports and downloading attachments
perform additional Firestore reads. Large collections take longer to load.

Run the offline integration tests:

```powershell
python -m unittest discover -s tools/bug-reports -p 'test_*.py' -v
```

Authentication and pagination follow the official
[Firestore REST authentication](https://firebase.google.com/docs/firestore/use-rest-api)
and [listDocuments](https://firebase.google.com/docs/firestore/reference/rest/v1/projects.databases.documents/listDocuments)
documentation.

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
5. Deploy the updated `screenshotPng` limit in `firestore.rules` before releasing this capture change.
6. Test one report on desktop and Android, verifying its Firestore document and
   both attachments. No live report or cloud configuration was submitted by this change.

## Payload

- Description (up to 4,000 characters), anonymous reporter UID, schema version and status.
- Client capture time in milliseconds; Firestore also provides authoritative `createTime`.
- Latest existing local `save.json` as **bytes** (`saveFile`), never parsed, overwritten,
  or truncated. Missing saves are recorded in metadata; saves above 512 KiB block
  submission with an explanatory message.
- Optional PNG screenshot as **bytes** (`screenshotPng`), captured before the menu
  opens and reduced to at most 320 KiB. It keeps up to 1440 pixels on its longest
  edge, stepping down only when needed to fit the Firestore document budget. It is
  the game framebuffer only. Capture
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
