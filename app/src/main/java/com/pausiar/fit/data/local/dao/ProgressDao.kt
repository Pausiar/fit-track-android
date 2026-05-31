package com.pausiar.fit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pausiar.fit.data.local.entity.SetProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {

    @Query("SELECT * FROM set_progress WHERE date = :date")
    fun observeForDate(date: String): Flow<List<SetProgressEntity>>

    @Query("SELECT * FROM set_progress WHERE date BETWEEN :start AND :end")
    fun observeBetween(start: String, end: String): Flow<List<SetProgressEntity>>

    /** All progress, used for history and streak calculations. */
    @Query("SELECT * FROM set_progress")
    fun observeAll(): Flow<List<SetProgressEntity>>

    @Query("SELECT * FROM set_progress")
    suspend fun allProgress(): List<SetProgressEntity>

    @Query(
        "SELECT * FROM set_progress WHERE exerciseId = :exerciseId " +
            "AND date = :date AND setIndex = :setIndex LIMIT 1"
    )
    suspend fun find(exerciseId: Long, date: String, setIndex: Int): SetProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: SetProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SetProgressEntity>)

    @Query("DELETE FROM set_progress WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM set_progress WHERE exerciseId = :exerciseId AND date = :date")
    suspend fun deleteForExerciseOnDate(exerciseId: Long, date: String)

    @Query("DELETE FROM set_progress WHERE date = :date")
    suspend fun deleteForDate(date: String)

    @Query("DELETE FROM set_progress WHERE date BETWEEN :start AND :end")
    suspend fun deleteBetween(start: String, end: String)

    @Query("DELETE FROM set_progress")
    suspend fun clearAll()
}
