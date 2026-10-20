# Building Ancient Terror

Use JDK 17 or 21 with the checked-in Gradle wrapper. Set `JAVA_HOME` to the JDK directory and configure the Android SDK location in `local.properties` (`sdk.dir`) or `ANDROID_HOME`. The Android build uses SDK 35.

From the repository root on Windows:

```powershell
.\gradlew.bat build
```

On Linux or macOS, use `./gradlew build`. This builds the included Android, desktop, and core modules and runs their checks and unit tests. The legacy HTML and iOS directories are not included in `settings.gradle`.

For only the debug APK or desktop compilation:

```powershell
.\gradlew.bat :android:assembleDebug
.\gradlew.bat :desktop:build
.\gradlew.bat :desktop:dist
```

Android APKs are written under `android/build/outputs/apk`. Release APKs are unsigned until a signing configuration is supplied.

The desktop distribution JAR is `desktop/build/libs/desktop-1.0-all.jar`.

## Automatic APK downloads

The **Android APK** GitHub Actions workflow runs on pushes to `main`, or manually
from **Actions → Android APK → Run workflow** with the `main` branch selected.
It runs the unit tests, builds a debug APK with test ads, verifies its application
ID and signing certificate, and uploads the APK plus SHA-256 checksum for 30 days.
Open a successful run and download its `Ancient-Terror-debug-...` artifact (also
linked in the run summary). Extract the ZIP on your phone and open the APK to
update the game. Keep the existing installation to retain its app data.

The repository secret `ANDROID_DEBUG_KEYSTORE_BASE64` must contain the base64 of
the **original computer's** `debug.keystore`. Its alias and passwords are the
standard Android debug defaults. The workflow restores it only in the runner's
temporary directory, verifies the resulting APK's certificate against the
expected public fingerprint in the workflow, and removes the key afterward.
Never commit keystores; they are ignored by Git. Replacing the secret with a new
debug key will fail the certificate check and will not update existing installs.

CI version codes are `100000 + GITHUB_RUN_NUMBER`; version names include the run
number and commit. Re-running a run keeps its version code. Once you install a
CI build, local builds using the old default code (`1717`) are older than it.
For a subsequent local installation, use `-PAPP_VERSION_CODE=<current-or-higher>`
with the same signing key. `-PAPP_VERSION_SUFFIX=<label>` optionally labels builds.
Normal local builds still use Android's default debug key. To use a copied key:

```powershell
$env:ANCIENT_TERROR_DEBUG_KEYSTORE = 'C:\private\debug.keystore'
.\gradlew.bat :android:installDebug -PAPP_VERSION_CODE=100001
```

Replace `100001` with at least the version code currently installed on the phone.
These APKs are test builds for direct installation, not Google Play releases.
The repository is public, so artifact downloads are available to signed-in
GitHub users who can access its Actions runs.

## Firebase configuration

The Android build reads `android/google-services.json`, falling back to the root `google-services.json`, and selects the client matching the Android application ID. Nonblank `FIREBASE_API_KEY`, `FIREBASE_APP_ID`, `FIREBASE_PROJECT_ID`, and `FIREBASE_GCM_SENDER_ID` Gradle properties override the corresponding JSON values. Release builds still require complete Firebase configuration and production AdMob IDs.

## Windows local-connection error

If Gradle fails with `Unable to establish loopback connection` and `UnixDomainSockets.connect0` in the stack trace, point Java's Unix-domain socket directory at an existing short local directory. For example, if `C:\TEMP` exists:

```powershell
$env:JAVA_TOOL_OPTIONS = "$env:JAVA_TOOL_OPTIONS -Djdk.net.unixdomain.tmpdir=C:\TEMP".Trim()
.\gradlew.bat build
```

This setting applies to the current PowerShell session and its Java child processes.
