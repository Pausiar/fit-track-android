#!/usr/bin/env bash
# Crea el almacén de claves de release de Fit Track.
# Uso: ./scripts/create-release-keystore.sh
set -euo pipefail
cd "$(dirname "$0")/.."

KEYSTORE="keystore/fit-track-release.jks"
ALIAS="fittrack"

if [ -f "$KEYSTORE" ]; then
  echo "Ya existe $KEYSTORE. No se sobrescribe."
  echo "Haz una copia de seguridad antes de crear otra clave."
  exit 1
fi

if ! command -v keytool >/dev/null 2>&1; then
  echo "keytool no está en el PATH. Instala un JDK 17 o superior."
  exit 1
fi

mkdir -p keystore
read -rsp "Contraseña del keystore (mínimo 6 caracteres): " STORE_PASS
echo
read -rsp "Repite la contraseña: " STORE_PASS_2
echo
if [ "$STORE_PASS" != "$STORE_PASS_2" ] || [ "${#STORE_PASS}" -lt 6 ]; then
  echo "Las contraseñas no coinciden o son demasiado cortas."
  exit 1
fi

read -rp "Nombre y apellidos u organización (CN): " CN
CN="${CN:-Fit Track}"
read -rp "Organización (O) [Pausiar]: " ORG
ORG="${ORG:-Pausiar}"
read -rp "País de dos letras (C) [ES]: " COUNTRY
COUNTRY="${COUNTRY:-ES}"

keytool -genkeypair \
  -keystore "$KEYSTORE" \
  -alias "$ALIAS" \
  -keyalg RSA \
  -keysize 4096 \
  -validity 10000 \
  -storepass "$STORE_PASS" \
  -keypass "$STORE_PASS" \
  -dname "CN=$CN, OU=$ORG, O=$ORG, C=$COUNTRY"

umask 077
cat > keystore.properties <<EOF
storeFile=$KEYSTORE
storePassword=$STORE_PASS
keyAlias=$ALIAS
keyPassword=$STORE_PASS
EOF
chmod 600 keystore.properties "$KEYSTORE"

cat <<'EOF'

Keystore creado. Guarda estos dos archivos en un lugar seguro y con copia:
  - keystore/fit-track-release.jks
  - keystore.properties

No los subas al repositorio. Para GitHub Actions, añade los secretos:
  FIT_KEYSTORE_BASE64  -> base64 -w0 keystore/fit-track-release.jks
  FIT_KEYSTORE_PASSWORD
  FIT_KEY_ALIAS        -> fittrack
  FIT_KEY_PASSWORD

Si pierdes esta clave no podrás publicar actualizaciones instalables sobre la
versión ya firmada.
EOF
