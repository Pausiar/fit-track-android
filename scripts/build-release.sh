#!/usr/bin/env bash
# Compila la APK de release, verifica la firma y muestra el checksum.
# Requiere JDK 17, Android SDK 34 y keystore.properties (o variables FIT_*).
set -euo pipefail
cd "$(dirname "$0")/.."

if [ ! -f keystore.properties ] && [ -z "${FIT_KEYSTORE_FILE:-}" ]; then
  echo "Falta keystore.properties o la variable FIT_KEYSTORE_FILE."
  echo "Ejecuta ./scripts/create-release-keystore.sh o configura los secretos de CI."
  exit 1
fi

./gradlew assembleRelease

APK="app/build/outputs/apk/release/app-release.apk"
if [ ! -f "$APK" ]; then
  echo "No se encontró $APK"
  exit 1
fi

APKSIGNER="${APKSIGNER:-${ANDROID_HOME:-$ANDROID_SDK_ROOT}/build-tools/34.0.0/apksigner}"
AAPT="${AAPT:-${ANDROID_HOME:-$ANDROID_SDK_ROOT}/build-tools/34.0.0/aapt}"

if [ -x "$APKSIGNER" ]; then
  "$APKSIGNER" verify --verbose --print-certs "$APK"
else
  echo "Aviso: apksigner no encontrado; no se pudo verificar la firma."
fi

VERSION=""
CODE=""
if [ -x "$AAPT" ]; then
  "$AAPT" dump badging "$APK" | head -1
  VERSION="$("$AAPT" dump badging "$APK" | sed -n "s/.*versionName='\([^']*\)'.*/\1/p" | head -1)"
  CODE="$("$AAPT" dump badging "$APK" | sed -n "s/.*versionCode='\([0-9]*\)'.*/\1/p" | head -1)"
fi

echo
sha256sum "$APK"
echo "Tamaño: $(stat -c%s "$APK") bytes"
echo "Salida: $APK"
echo
echo "Para publicar en la web:"
if [ -n "$VERSION" ] && [ -n "$CODE" ]; then
  echo "  python3 scripts/update_release.py --apk \"$APK\" --version $VERSION --version-code $CODE --update-html"
else
  echo "  python3 scripts/update_release.py --apk \"$APK\" --version <versión> --version-code <código> --update-html"
fi
