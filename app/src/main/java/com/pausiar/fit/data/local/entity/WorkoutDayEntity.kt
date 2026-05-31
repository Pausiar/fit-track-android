package com.pausiar.fit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A day of the week template (e.g. Monday = Push). The [id] equals [dayOfWeek]
 * (1 = Monday ... 7 = Sunday) so the weekly routine maps directly to ISO day numbers.
 */
@Entity(tableName = "workout_day")
data class WorkoutDayEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val focus: String,
    val dayOfWeek: Int
)
