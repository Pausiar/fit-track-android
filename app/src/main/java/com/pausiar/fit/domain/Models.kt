package com.pausiar.fit.domain

import com.pausiar.fit.data.local.entity.ExerciseEntity

/** UI-facing progress for a single exercise on a concrete date. */
data class ExerciseProgress(
    val exercise: ExerciseEntity,
    val completedSetIndices: Set<Int>
) {
    val completedCount: Int get() = completedSetIndices.count { it < exercise.targetSets }
    val totalSets: Int get() = exercise.targetSets
    val isComplete: Boolean get() = completedCount >= totalSets && totalSets > 0
}

/** Aggregated progress for a whole day on a concrete date. */
data class DayProgress(
    val dayId: Int,
    val dayName: String,
    val focus: String,
    val dateIso: String,
    val completedSets: Int,
    val totalSets: Int,
    val completedExercises: Int,
    val totalExercises: Int
) {
    val percentage: Int
        get() = if (totalSets == 0) 0 else (completedSets * 100) / totalSets
    val fraction: Float
        get() = if (totalSets == 0) 0f else completedSets.toFloat() / totalSets
}

/** A single dated history entry used by the history screen. */
data class HistoryEntry(
    val dateIso: String,
    val dayName: String,
    val focus: String,
    val completedSets: Int,
    val totalSets: Int
) {
    val percentage: Int
        get() = if (totalSets == 0) 0 else (completedSets * 100) / totalSets
}

/** Overall stats shown in the history screen. */
data class HistoryStats(
    val totalCompletedSets: Int,
    val completedDays: Int,
    val currentStreak: Int,
    val bestStreak: Int
)
