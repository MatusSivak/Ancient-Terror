# Bug reports

## Automatic crash reports

The game sends its first handled or uncaught Java failure per session to
`crashReports/{uuid}` in the same Firebase project. It uses the manual bug-report
payload and authentication: the latest saved game, capture timestamp, app/platform
version, language, framebuffer dimensions, density, and attachment status. The
description contains the exception stack trace (limited to 4,000 characters), and
metadata also includes the failing thread name. The save is never overwritten.

Screenshot capture is best-effort on the render thread; background-thread crashes
or an unavailable GL context have `screenshotStatus: unavailable`. Missing,
unreadable, or oversized saves do not prevent a crash report; their status is
recorded and the attachment is omitted. Existing attachment limits still apply.

Reports are written to local `pending-crash-reports/` before the first upload.
Uploads do not require the render loop. Failed/interrupted uploads retry on the
next launch or resume with the original ID, capture time and attachments, and
local records are removed only after Firebase acknowledges them. Existing crash
handling still runs. Native crashes, OS process kills, and failures before game
initialization cannot be captured by this Java handler; exhausted memory or
unwritable storage can also prevent capture.

Before releasing, merge the **crashReports** block from `firestore.rules` into
the deployed Firestore rules, preserving other rules, and exempt `saveFile` and
`screenshotPng` from indexing for this collection too. No cloud rules are deployed
by this code change. The local viewer displays both `bugReports` and `crashReports` together; use
**Report type** to show all reports, only bug reports, or only crash reports.

### Testing crash uploads on desktop

From the repository root, run:

```powershell
.\gradlew.bat :desktop:run --args="--crash-report-test"
```

Wait for the game to finish loading, then press **F8** with the game window
focused. This throws an intentional exception through the normal render error
handler. Desktop's existing handler logs the error and lets the game continue;
the test does not need to close the process. F8 is only enabled by this launch
argument (or the `-Dancientterror.crashReportTest=true` desktop VM option).

The console should show `[CrashReports] Uploaded crashReports/<uuid>`. In Firebase
Console > Firestore Database > Data > `crashReports`, open that UUID and check:

- `description` contains `TEST crash report: intentional F8 failure` and a stack trace.
- `status` is `new`, `schemaVersion` is `1`, and `reporterUid` is populated.
- `metadata` has platform, app version, language, screen, density, and thread.
- `saveFile` contains the existing save (if present and within the size limit).
- `screenshotPng` contains the game screenshot if capture succeeded.

For an offline/restart test, start a **fresh test session**, disconnect networking,
press F8, and check `assets/pending-crash-reports/<uuid>.json`. Close the game,
reconnect, then launch normally with `.\gradlew.bat :desktop:run`. Verify the same
UUID uploads and the pending file disappears. Only the first failure per session
is captured, so restart before each separate test. A `configuration` failure means
checking the Firebase project configuration, anonymous authentication and rules;
a `network` failure indicates a transport/server failure. These tests create real
documents in the configured Firebase project. They do not deploy rules.

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

The viewer loads all summary pages from both `bugReports` and `crashReports`.
Use **Report type** to choose All reports, Bug reports, or Crash reports; each entry
also displays its type. Text search, status/platform filters, and date sorting
work across both collections. Crash details show the exception and stack trace.
Screenshots, downloads, status changes, and deletion operate on the selected
report in its original collection. If one collection fails to load, reports from
the other remain available with an explicit incomplete-results warning. Select a report for its description, full-size
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

Offline browser checks are in `test_app.cjs`. With Node.js, Playwright, and its
Chromium browser available, run `node --test tools/bug-reports/test_app.cjs`.
Alternatively, set `VIEWER_TEST_BROWSER` to an installed Chrome/Chromium executable.
These checks mock both collections; they do not read or modify live reports.

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
