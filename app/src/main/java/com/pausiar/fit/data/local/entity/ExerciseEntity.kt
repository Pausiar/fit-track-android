package com.pausiar.fit.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * An exercise belonging to a [WorkoutDayEntity]. Targets (sets/reps) are part of the
 * template; actual per-date completion is tracked in [SetProgressEntity].
 */
@Entity(
    tableName = "exercise",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["dayId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("dayId")]
)
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayId: Int,
    val name: String,
    val muscleGroup: String,
    val targetSets: Int,
    val targetReps: String,
    val notes: String = "",
    val orderIndex: Int = 0
)
