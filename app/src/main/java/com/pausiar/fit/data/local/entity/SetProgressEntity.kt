package com.pausiar.fit.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a single completed set for an exercise on a concrete calendar [date].
 * A row only exists when the set is completed, so counting rows == completed sets.
 * The (exerciseId, date, setIndex) triple is unique.
 */
@Entity(
    tableName = "set_progress",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["exerciseId", "date", "setIndex"], unique = true),
        Index("date")
    ]
)
data class SetProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: Long,
    val date: String, // ISO yyyy-MM-dd
    val setIndex: Int,
    val completedAt: Long
)
