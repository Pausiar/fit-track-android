package com.pausiar.fit

import android.app.Application
import com.pausiar.fit.data.local.AppDatabase
import com.pausiar.fit.data.repository.RoutineRepository

/**
 * App entry point. Acts as a tiny service locator that owns the database and
 * repository, avoiding a DI framework for such a small app.
 */
class FitApplication : Application() {

    val repository: RoutineRepository by lazy {
        val db = AppDatabase.get(this)
        RoutineRepository(db.workoutDao(), db.progressDao())
    }
}
