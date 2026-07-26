#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "${PROJECT_ROOT}"

PACKAGE_NAME="sk.sivak.eldritchhorror.android"
ACTIVITY_NAME=".GameActivity"
UNSIGNED_APK="android/build/outputs/apk/release/android-release-unsigned.apk"
SIGNED_APK="android/build/outputs/apk/release/android-release-smoke.apk"
LOG_FILE="android/build/reports/release-smoke-logcat.txt"

FIREBASE_API_KEY="${FIREBASE_API_KEY:-ci_api_key}"
FIREBASE_APP_ID="${FIREBASE_APP_ID:-1:1234567890:android:abcdef}"
FIREBASE_PROJECT_ID="${FIREBASE_PROJECT_ID:-ancient-terror-ci}"
FIREBASE_GCM_SENDER_ID="${FIREBASE_GCM_SENDER_ID:-1234567890}"

./gradlew :android:assembleRelease --no-daemon \
  -PFIREBASE_API_KEY="${FIREBASE_API_KEY}" \
  -PFIREBASE_APP_ID="${FIREBASE_APP_ID}" \
  -PFIREBASE_PROJECT_ID="${FIREBASE_PROJECT_ID}" \
  -PFIREBASE_GCM_SENDER_ID="${FIREBASE_GCM_SENDER_ID}"

if [[ ! -f "${UNSIGNED_APK}" ]]; then
  echo "Missing unsigned release APK at ${UNSIGNED_APK}" >&2
  exit 1
fi

mkdir -p "${HOME}/.android"
DEBUG_KEYSTORE="${HOME}/.android/debug.keystore"
if [[ ! -f "${DEBUG_KEYSTORE}" ]]; then
  keytool -genkeypair -v \
    -keystore "${DEBUG_KEYSTORE}" \
    -storepass android \
    -alias androiddebugkey \
    -keypass android \
    -dname "CN=Android Debug,O=Android,C=US" \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000
fi

BUILD_TOOLS_DIR="$(find "${ANDROID_HOME}/build-tools" -mindepth 1 -maxdepth 1 -type d | sort -V | tail -n1)"
if [[ -z "${BUILD_TOOLS_DIR}" ]]; then
  echo "Android build-tools not found under ${ANDROID_HOME}/build-tools" >&2
  exit 1
fi

"${BUILD_TOOLS_DIR}/apksigner" sign \
  --ks "${DEBUG_KEYSTORE}" \
  --ks-key-alias androiddebugkey \
  --ks-pass pass:android \
  --key-pass pass:android \
  --out "${SIGNED_APK}" \
  "${UNSIGNED_APK}"

adb install -r "${SIGNED_APK}"
adb logcat -c
adb shell am start -n "${PACKAGE_NAME}/${ACTIVITY_NAME}"
sleep 30

if ! adb shell pidof "${PACKAGE_NAME}" >/dev/null; then
  echo "App process did not stay alive after launch." >&2
  exit 1
fi

adb logcat -d > "${LOG_FILE}"
if grep -E "FATAL EXCEPTION|AndroidRuntime: FATAL|Process: ${PACKAGE_NAME}" "${LOG_FILE}" >/dev/null; then
  echo "Release smoke failed, fatal runtime crash found in logcat." >&2
  exit 1
fi

echo "Release smoke passed."
