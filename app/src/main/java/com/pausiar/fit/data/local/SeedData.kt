package com.pausiar.fit.data.local

import com.pausiar.fit.data.local.entity.ExerciseEntity
import com.pausiar.fit.data.local.entity.WorkoutDayEntity

/** Initial routine that ships pre-loaded with the app (matches the user's plan). */
object SeedData {

    val days: List<WorkoutDayEntity> = listOf(
        WorkoutDayEntity(1, "Lunes", "Fuerza de piernas y core", 1),
        WorkoutDayEntity(2, "Martes", "Empuje y tirón", 2),
        WorkoutDayEntity(3, "Miércoles", "Core y movilidad", 3),
        WorkoutDayEntity(4, "Jueves", "Potencia", 4),
        WorkoutDayEntity(5, "Viernes", "Estabilidad y fuerza", 5),
        WorkoutDayEntity(6, "Sábado", "Descanso", 6),
        WorkoutDayEntity(7, "Domingo", "Descanso", 7)
    )

    /** Helper to build the exercise list with auto-incrementing order per day. */
    private fun ex(
        dayId: Int,
        order: Int,
        name: String,
        muscle: String,
        sets: Int,
        reps: String,
        notes: String = ""
    ) = ExerciseEntity(
        dayId = dayId,
        name = name,
        muscleGroup = muscle,
        targetSets = sets,
        targetReps = reps,
        notes = notes,
        orderIndex = order
    )

    val exercises: List<ExerciseEntity> = listOf(
        // Lunes — Fuerza de piernas y core
        ex(1, 0, "Sentadilla goblet pausada", "Piernas", 4, "12", "Circuito A · 4 vueltas"),
        ex(1, 1, "Peso muerto rumano a una pierna", "Piernas / Equilibrio", 4, "10 por lado", "Circuito A · 4 vueltas"),
        ex(1, 2, "Swing ruso", "Potencia de cadera", 4, "20", "Circuito A · 4 vueltas"),
        ex(1, 3, "Flexiones", "Empuje", 4, "12", "Circuito A · 4 vueltas"),
        ex(1, 4, "Farmer carry", "Agarre / Core", 3, "45 s", "Circuito B · 3 vueltas"),
        ex(1, 5, "Plancha con arrastre de kettlebell", "Core", 3, "30 s", "Circuito B · 3 vueltas"),
        ex(1, 6, "Plancha lateral", "Oblicuos", 3, "30 s por lado", "Circuito B · 3 vueltas"),

        // Martes — Empuje y tirón
        ex(2, 0, "Clean + press", "Potencia / Hombro", 4, "10 por lado", "Circuito A · 4 vueltas"),
        ex(2, 1, "Remo unilateral", "Espalda", 4, "12 por lado", "Circuito A · 4 vueltas"),
        ex(2, 2, "Floor press", "Pecho / Tríceps", 4, "12 por lado", "Circuito A · 4 vueltas"),
        ex(2, 3, "Flexiones", "Empuje", 4, "10", "Circuito A · 4 vueltas"),
        ex(2, 4, "Halo", "Hombros / Core", 3, "10 por lado", "Circuito B · 3 vueltas"),
        ex(2, 5, "Curl + press", "Bíceps / Hombro", 3, "10", "Circuito B · 3 vueltas"),
        ex(2, 6, "Dead bug", "Core", 3, "12 por lado", "Circuito B · 3 vueltas"),

        // Miércoles — Core y movilidad
        ex(3, 0, "Turkish get-up", "Core / Técnica", 4, "4 por lado", "Técnica"),
        ex(3, 1, "Windmill", "Core / Técnica", 4, "8 por lado", "Técnica"),
        ex(3, 2, "Marcha del granjero unilateral", "Core / Estabilidad", 4, "45 s por lado", "Técnica"),
        ex(3, 3, "Movilidad de caderas", "Movilidad", 1, "5 min", "Movilidad · 20 min totales"),
        ex(3, 4, "Movilidad de dorsales", "Movilidad", 1, "5 min", "Movilidad · 20 min totales"),
        ex(3, 5, "Movilidad de hombros", "Movilidad", 1, "5 min", "Movilidad · 20 min totales"),
        ex(3, 6, "Movilidad de tobillos", "Movilidad", 1, "5 min", "Movilidad · 20 min totales"),

        // Jueves — Potencia
        ex(4, 0, "Swing ruso", "Potencia de cadera", 5, "20", "Circuito A · 5 vueltas"),
        ex(4, 1, "Thruster", "Piernas / Hombros", 5, "12", "Circuito A · 5 vueltas"),
        ex(4, 2, "High pull", "Espalda / Potencia", 5, "12 por lado", "Circuito A · 5 vueltas"),
        ex(4, 3, "Flexiones", "Empuje", 5, "12", "Circuito A · 5 vueltas"),
        ex(4, 4, "Zancadas caminando", "Piernas", 3, "12 por pierna", "Circuito B · 3 vueltas"),
        ex(4, 5, "Russian twist", "Core", 3, "20", "Circuito B · 3 vueltas"),

        // Viernes — Estabilidad y fuerza
        ex(5, 0, "Step-up", "Piernas", 4, "12 por pierna", "Circuito A · 4 vueltas"),
        ex(5, 1, "Peso muerto + remo", "Cadena posterior / Espalda", 4, "10 por lado", "Circuito A · 4 vueltas"),
        ex(5, 2, "Push press", "Hombros / Potencia", 4, "10 por lado", "Circuito A · 4 vueltas"),
        ex(5, 3, "Flexiones", "Empuje", 4, "10", "Circuito A · 4 vueltas"),
        ex(5, 4, "Farmer carry unilateral", "Core / Agarre", 3, "45 s", "Circuito B · 3 vueltas"),
        ex(5, 5, "Plancha", "Core", 3, "45 s", "Circuito B · 3 vueltas"),
        ex(5, 6, "Hollow hold", "Core", 3, "30 s", "Circuito B · 3 vueltas")
    )
}
