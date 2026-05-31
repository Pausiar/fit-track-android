package com.pausiar.fit.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pausiar.fit.data.local.entity.ExerciseEntity
import com.pausiar.fit.data.local.entity.WorkoutDayEntity
import com.pausiar.fit.data.local.relation.DayWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Transaction
    @Query("SELECT * FROM workout_day ORDER BY dayOfWeek")
    fun observeDaysWithExercises(): Flow<List<DayWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout_day WHERE id = :dayId")
    fun observeDayWithExercises(dayId: Int): Flow<DayWithExercises?>

    @Query("SELECT COUNT(*) FROM workout_day")
    suspend fun dayCount(): Int

    @Query("SELECT * FROM exercise WHERE dayId = :dayId ORDER BY orderIndex")
    suspend fun exercisesForDay(dayId: Int): List<ExerciseEntity>

    @Query("SELECT * FROM exercise ORDER BY dayId, orderIndex")
    suspend fun allExercises(): List<ExerciseEntity>

    @Query("SELECT * FROM workout_day ORDER BY dayOfWeek")
    suspend fun allDays(): List<WorkoutDayEntity>

    @Query("SELECT MAX(orderIndex) FROM exercise WHERE dayId = :dayId")
    suspend fun maxOrder(dayId: Int): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDays(days: List<WorkoutDayEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity): Long

    @Update
    suspend fun updateExercise(exercise: ExerciseEntity)

    @Update
    suspend fun updateExercises(exercises: List<ExerciseEntity>)

    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity)

    @Query("DELETE FROM exercise")
    suspend fun clearExercises()

    @Query("DELETE FROM workout_day")
    suspend fun clearDays()
}
