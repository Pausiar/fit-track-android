Convierte la rutina que te voy a pegar al JSON importable por Jose Luis Fit.

Devuelve solo JSON valido, sin markdown, sin comentarios y sin texto extra.

Reglas del formato:
- La raiz debe tener: schemaVersion, type y days.
- Usa: "schemaVersion": 1
- Usa: "type": "routine"
- `days` es un array.
- Cada dia debe tener:
  - `dayOfWeek` (1 = lunes, 7 = domingo)
  - `name`
  - `focus`
  - `exercises`
- Cada ejercicio debe tener:
  - `name`
  - `muscleGroup`
  - `targetSets`
  - `targetReps`
  - `notes` (opcional)
- Si un circuito tiene 4 vueltas, cada ejercicio de ese circuito debe llevar `targetSets: 4`.
- `targetReps` debe ser texto libre como `12`, `10 por lado`, `45 s`, `5 min`, etc.
- Si faltan dias, omitelo; la app los rellenara como descanso.
- Mantén el orden original de dias y ejercicios.

Ejemplo minimo del formato esperado:
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

Ahora convierte esta rutina:
[PEGA_AQUI_LA_RUTINA]
