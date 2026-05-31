package com.pausiar.fit.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.pausiar.fit.data.local.entity.ExerciseEntity
import com.pausiar.fit.data.local.entity.WorkoutDayEntity

/** A workout day together with all of its exercises. */
data class DayWithExercises(
    @Embedded val day: WorkoutDayEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "dayId"
    )
    val exercises: List<ExerciseEntity>
) {
    /** Exercises sorted by their template order. */
    val orderedExercises: List<ExerciseEntity>
        get() = exercises.sortedBy { it.orderIndex }

    /** Total target sets across the whole day. */
    val totalSets: Int
        get() = exercises.sumOf { it.targetSets }
}
