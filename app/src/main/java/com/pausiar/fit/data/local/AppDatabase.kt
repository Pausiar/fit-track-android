package com.pausiar.fit.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pausiar.fit.data.local.dao.ProgressDao
import com.pausiar.fit.data.local.dao.WorkoutDao
import com.pausiar.fit.data.local.entity.ExerciseEntity
import com.pausiar.fit.data.local.entity.SetProgressEntity
import com.pausiar.fit.data.local.entity.WorkoutDayEntity

@Database(
    entities = [
        WorkoutDayEntity::class,
        ExerciseEntity::class,
        SetProgressEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
    abstract fun progressDao(): ProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pausiar_fit.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
