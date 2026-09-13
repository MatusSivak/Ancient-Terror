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

## Firebase configuration

The Android build reads `android/google-services.json`, falling back to the root `google-services.json`, and selects the client matching the Android application ID. Nonblank `FIREBASE_API_KEY`, `FIREBASE_APP_ID`, `FIREBASE_PROJECT_ID`, and `FIREBASE_GCM_SENDER_ID` Gradle properties override the corresponding JSON values. Release builds still require complete Firebase configuration and production AdMob IDs.

## Windows local-connection error

If Gradle fails with `Unable to establish loopback connection` and `UnixDomainSockets.connect0` in the stack trace, point Java's Unix-domain socket directory at an existing short local directory. For example, if `C:\TEMP` exists:

```powershell
$env:JAVA_TOOL_OPTIONS = "$env:JAVA_TOOL_OPTIONS -Djdk.net.unixdomain.tmpdir=C:\TEMP".Trim()
.\gradlew.bat build
```

This setting applies to the current PowerShell session and its Java child processes.
