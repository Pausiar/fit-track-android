# Jose Luis Fit

App Android **100% offline** para seguir tu rutina de entrenamiento semanal. Cada día tiene sus ejercicios, y cada ejercicio una casilla por serie. Marcas las series conforme las completas y todo se guarda automáticamente en el dispositivo, asociado a la fecha concreta.

> Construida con Kotlin + Jetpack Compose + Room. Sin backend, sin login, sin internet.

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
- **Rutinas personalizadas por JSON**: puedes importar una rutina simple creada a mano o generada por una IA.
- **Ajustes de reseteo**: resetear hoy, la semana, todo el progreso o restaurar la rutina por defecto.
- **Modo oscuro permanente** con la paleta de la marca.

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

> Requiere el Android SDK instalado. La primera ejecución genera la base de datos y siembra la rutina por defecto.

---

## JSON de rutina personalizada

La app acepta dos formatos al importar:

- **Backup completo**: días, ejercicios y progreso.
- **Rutina personalizada**: solo la estructura de entrenamiento. Al importarla, la app reemplaza la rutina actual y limpia el progreso.

Formato recomendado para una rutina personalizada:

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
- `targetSets`: número de casillas que aparecerán para ese ejercicio.
- `targetReps`: texto libre (`12`, `10 por lado`, `45 s`, etc.).
- `notes`: opcional. Sirve para indicar circuito, bloque o contexto.
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

100% offline · Tus datos nunca salen del dispositivo.
