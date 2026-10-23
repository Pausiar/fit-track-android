# Fit Track

App Android **100% offline** para seguir tu rutina de entrenamiento semanal. Cada día tiene sus ejercicios y cada ejercicio una casilla por serie. Marcas las series conforme las completas y todo se guarda automáticamente en el dispositivo, asociado a la fecha concreta.

> Kotlin + Jetpack Compose + Room. Sin backend, sin login, sin permiso de Internet.

- **Web oficial:** <https://pausiar.github.io/fit-track-android/>
- **Descarga:** <https://pausiar.github.io/fit-track-android/download.html>
- **Repositorio:** <https://github.com/Pausiar/fit-track-android>
- **Versión actual:** 1.1.0 (versionCode 2), APK firmada con clave de release.
- **Paquete:** `com.pausiar.fit` · Android 8.0+ (API 26+).

---

## Web de descarga

La landing y las páginas legales son estáticas y se publican con GitHub Pages desde `docs/`. El diseño replica la identidad visual de la app (fondo `#080A0E`, acento `#E8FF00`, cyan `#00D4FF`, naranja `#FF4D00`, tarjetas de 14–18 px y tipografía del sistema).

### Contenido de la web

```
docs/
├── index.html                  # Landing
├── download.html               # Descarga, instalación y verificación
├── privacy.html                # Política de privacidad (RGPD/LOPDGDD)
├── cookies.html                # Política de cookies (sin cookies)
├── legal-notice.html           # Aviso legal (LSSI-CE)
├── terms.html                  # Términos de uso y aviso de salud
├── 404.html                    # Página de error
├── release.json                # Metadatos de la versión publicada
├── downloads/
│   ├── fit-track-1.1.0.apk     # APK firmada
│   └── fit-track-1.1.0.apk.sha256
├── styles.css
├── script.js
├── favicon.svg
├── icon-192.png / icon-512.png
├── apple-touch-icon.png
├── og-image.png
├── site.webmanifest
├── sitemap.xml
├── robots.txt
└── .well-known/security.txt
```

La web no usa cookies, analytics, formularios, fuentes externas ni CDN. El JavaScript solo carga `release.json`, actualiza los enlaces de descarga, copia hashes al portapapeles y aplica animaciones de aparición. Tiene CSP estricta y no contiene estilos ni scripts inline.

### Publicar una nueva APK en la web

1. Compila y firma la nueva versión (ver [Firma de release](#firma-de-release)).
2. Ejecuta:

   ```bash
   python3 scripts/update_release.py \
     --apk app/build/outputs/apk/release/app-release.apk \
     --version 1.2.0 \
     --version-code 3 \
     --update-html
   ```

   El script copia el APK a `docs/downloads/`, calcula tamaño y SHA-256, escribe el `.sha256`, actualiza `docs/release.json` y el fallback sin JavaScript de las páginas.

3. Revisa la web y los metadatos:

   ```bash
   python3 scripts/check-site.py
   ```

4. Haz commit de `docs/downloads/`, `docs/release.json` y los cambios de las páginas. El workflow `pages.yml` valida y despliega `docs/` en cada push a `main`.

### Workflows

- **`.github/workflows/pages.yml`**: valida `docs/` con `scripts/check-site.py` y publica en GitHub Pages.
- **`.github/workflows/android-release.yml`**: al empujar un tag `vX.Y.Z`, compila la APK de release con los secretos de firma, verifica el certificado con `apksigner`, crea una GitHub Release con APK + `.sha256`, actualiza `docs/` en `main` y despliega Pages. Así una publicación posterior en `main` no revierte la web a una versión anterior.

---

## Firma de release

La APK pública **no** se firma con la clave de depuración de Android. Se usa una clave RSA-4096 propia del proyecto:

- Alias: `fittrack`
- Certificado: `CN=Fit Track, OU=Pausiar, O=Pausiar, C=ES`
- SHA-256 del certificado:
  `5F:18:5F:6E:D8:8A:57:35:7E:0C:52:10:07:A6:44:F7:BD:1C:7C:56:02:16:10:B2:CB:BB:38:F3:40:3B:85:82`

El certificado público está en [`docs/fit-track-release.pem`](docs/fit-track-release.pem) para que cualquiera pueda compararlo con `apksigner verify --print-certs`.

> **Importante:** el `keystore` y su contraseña no se versionan. Están en `keystore/` y `keystore.properties`, ignorados por Git. Guarda una copia segura: sin esa clave no podrás publicar actualizaciones que se instalen encima de la versión firmada.

### Configuración local

Ya hay una clave generada en este workspace. Si necesitas crear otra (por ejemplo, en otro equipo), ejecuta:

```bash
./scripts/create-release-keystore.sh
```

El script pide la contraseña, crea `keystore/fit-track-release.jks` y `keystore.properties` con permisos `600`, y recuerda los secretos que hay que añadir a GitHub.

Para compilar, firmar y verificar:

```bash
./scripts/build-release.sh
```

O directamente:

```bash
./gradlew assembleRelease
```

La configuración de `app/build.gradle.kts` lee `keystore.properties` o, si no existe, estas variables de entorno:

| Variable | Contenido |
|---|---|
| `FIT_KEYSTORE_FILE` | Ruta al `.jks` |
| `FIT_KEYSTORE_PASSWORD` | Contraseña del almacén |
| `FIT_KEY_ALIAS` | `fittrack` |
| `FIT_KEY_PASSWORD` | Contraseña de la clave |

La salida queda en `app/build/outputs/apk/release/app-release.apk`. Verifica:

```bash
apksigner verify --verbose --print-certs app/build/outputs/apk/release/app-release.apk
sha256sum app/build/outputs/apk/release/app-release.apk
```

### Secretos de GitHub Actions

En **Settings → Secrets and variables → Actions** del repositorio:

| Secreto | Valor |
|---|---|
| `FIT_KEYSTORE_BASE64` | `base64 -w0 keystore/fit-track-release.jks` |
| `FIT_KEYSTORE_PASSWORD` | Contraseña del almacén |
| `FIT_KEY_ALIAS` | `fittrack` |
| `FIT_KEY_PASSWORD` | Contraseña de la clave |

Para publicar una versión:

```bash
git tag v1.2.0
git push origin v1.2.0
```

El tag debe coincidir con `versionName` en `app/build.gradle.kts` (`1.2.0`). El workflow falla si no coincide.

### Por qué Android sigue avisando

Una firma de release válida evita el error de «certificado de depuración» y permite actualizaciones coherentes, pero **Android y Play Protect avisan igualmente al instalar una APK fuera de Play Store**. Ese aviso de «origen desconocido» o «desarrollador no verificado» es una política de la plataforma y solo desaparece distribuyendo por Google Play. La web publica SHA-256 y huella del certificado para que cualquiera pueda comprobar que el archivo es el oficial.

---

## Legalidad

La web está revisada para el marco español/europeo:

- **Aviso legal (LSSI-CE):** titular identificado como **Pausiar**, proyecto personal sin actividad comercial; contacto mediante GitHub.
- **Privacidad (RGPD/LOPDGDD):** responsable, finalidades, bases jurídicas, conservación, destinatarios, transferencias, derechos y AEPD.
- **Cookies:** no se usan cookies ni almacenamiento del navegador; no se muestra banner porque no es necesario.
- **Términos:** licencia de uso, instalación fuera de tienda, datos locales, backups, salud, garantías y responsabilidad.
- **Transparencia de descarga:** versión, tamaño, SHA-256, huella del certificado, changelog y guía de verificación.
- **Seguridad:** `/.well-known/security.txt` para comunicar vulnerabilidades.

La app no es un producto sanitario ni sustituye asesoramiento médico. Los términos incluyen un aviso de salud y no excluyen derechos imperativos de consumidores.

> Si el proyecto se monetiza, cambia de titularidad o se publica en Google Play, habrá que actualizar el aviso legal, la privacidad y los términos con los datos fiscales y de contacto correspondientes.

---

## Características

- **Tracking por series**: una casilla grande y táctil por serie, con animación al marcar.
- **Guardado automático y local**: cada marca se persiste al instante en Room (SQLite). El progreso sobrevive al cerrar la app.
- **Por fecha**: cada día del calendario tiene su propio progreso, así la rutina semanal se repite sin perder el historial.
- **Panel de hoy**: resumen del entrenamiento del día (porcentaje, ejercicios y series).
- **Vista semanal**: estado de los 7 días de la semana en curso.
- **Historial y rachas**: días entrenados, series totales, racha actual y mejor racha.
- **Editor de rutina**: añadir, editar y eliminar ejercicios por día.
- **Copia de seguridad**: exportar e importar todos los datos en JSON.
- **Rutinas personalizadas por JSON**: rutina simple creada a mano o generada por una IA.
- **Ajustes de reseteo**: resetear hoy, la semana, todo el progreso o restaurar la rutina por defecto.
- **Modo oscuro permanente** con la paleta de la marca.
- **Sin permiso de Internet** y con copias automáticas de Android desactivadas.

### Cambios en 1.1.0

- Firma de release oficial (ya no se usa el certificado de depuración).
- Nombre unificado como **Fit Track** en la app y la web.
- `android:allowBackup="false"` para reforzar el carácter local de los datos.
- Versión visible generada desde `BuildConfig.VERSION_NAME`.
- Nueva web con descarga verificable, checksum y huella del certificado.
- Workflows de CI para firmar, publicar y desplegar.

> La versión 1.0 usaba firma debug. Android no permite actualizar entre firmas distintas: hay que desinstalarla e instalar la 1.1.0. Si se quiere conservar el progreso, exportar el JSON antes e importarlo después.

---

## Arquitectura

Arquitectura limpia por capas, sin librería de DI (service locator manual en `FitApplication`).

```
UI (Compose)  ->  ViewModel (StateFlow)  ->  Repository  ->  Data (Room DAO + Entities)
```

### Modelo de persistencia

El progreso se guarda de forma dispersa: **existe una fila `SetProgressEntity` solo cuando una serie está completada**. Marcar/desmarcar = insertar/borrar fila. La clave única `(exerciseId, date, setIndex)` garantiza que cada fecha tenga progreso independiente.

### Estructura del proyecto

```
app/src/main/java/com/pausiar/fit/
├── FitApplication.kt            # Application + repositorio (service locator)
├── MainActivity.kt              # Edge-to-edge + tema + NavHost
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt       # Room @Database (singleton)
│   │   ├── SeedData.kt          # Rutina inicial (7 días)
│   │   ├── dao/                 # WorkoutDao, ProgressDao
│   │   ├── entity/              # WorkoutDay, Exercise, SetProgress
│   │   └── relation/            # DayWithExercises
│   └── repository/
│       └── RoutineRepository.kt # Única fuente de verdad (incluye export/import JSON)
├── domain/Models.kt             # Modelos de UI (ExerciseProgress, DayProgress, ...)
├── util/DateUtils.kt            # Helpers de fechas ISO
└── ui/
    ├── AppViewModelFactory.kt   # Wiring de ViewModels
    ├── theme/                   # Color, Type, Theme
    ├── components/Common.kt     # Componentes reutilizables
    ├── navigation/              # Routes + FitNavHost
    ├── dashboard/               # Pantalla principal
    ├── day/                     # Pantalla de entrenamiento (tracking)
    ├── history/                 # Historial y estadísticas
    ├── edit/                    # Editor de rutina
    └── settings/                # Ajustes / backup / reset
```

---

## Stack técnico

| Componente | Versión |
|---|---|
| Kotlin | 2.0.20 |
| Android Gradle Plugin | 8.5.2 |
| Gradle | 8.9 (wrapper incluido) |
| Compose BOM | 2024.09.02 |
| Room | 2.6.1 (con KSP) |
| Navigation Compose | 2.8.2 |
| minSdk / target / compile | 26 / 34 / 34 |
| JVM target | 17 |

---

## Cómo ejecutar

1. Abre la carpeta del proyecto en **Android Studio** (Ladybug o superior).
2. Deja que Gradle sincronice y descargue dependencias.
3. Conecta un dispositivo o crea un emulador (API 26+).
4. Pulsa **Run ▶**.

Con Gradle desde terminal:

```bash
./gradlew assembleDebug
# o
./gradlew assembleRelease
```

La primera ejecución genera la base de datos y siembra la rutina por defecto. Para compilar en CI o en un equipo limpio hacen falta JDK 17 y Android SDK 34.

---

## JSON de rutina personalizada

La app acepta dos formatos al importar:

- **Backup completo**: días, ejercicios y progreso.
- **Rutina personalizada**: solo la estructura de entrenamiento. Al importarla, la app reemplaza la rutina actual y limpia el progreso.

Formato recomendado:

```json
{
  "schemaVersion": 1,
  "type": "routine",
  "days": [
    {
      "dayOfWeek": 1,
      "name": "Lunes",
      "focus": "Piernas y core",
      "exercises": [
        {
          "name": "Sentadilla goblet",
          "muscleGroup": "Piernas",
          "targetSets": 4,
          "targetReps": "12",
          "notes": "Circuito A · 4 vueltas"
        }
      ]
    }
  ]
}
```

Reglas útiles:

- `dayOfWeek`: 1 = lunes, 7 = domingo.
- `targetSets`: número de casillas para ese ejercicio.
- `targetReps`: texto libre (`12`, `10 por lado`, `45 s`, etc.).
- `notes`: opcional.
- Si omites días, la app los rellena como `Descanso`.

Ejemplos listos para usar:

- `examples/jose-luis-routine.json`
- `examples/ai-routine-prompt.md`

---

## Ideas para ampliar

- Reordenar ejercicios con arrastrar y soltar.
- Crear/editar días y enfoques personalizados.
- Gráficas de volumen y progreso por grupo muscular.
- Temporizador de descanso entre series.
- Recordatorios/notificaciones diarias.
- Sincronización opcional cifrada.

---

100% offline · Tus datos nunca salen del dispositivo · © Pausiar
