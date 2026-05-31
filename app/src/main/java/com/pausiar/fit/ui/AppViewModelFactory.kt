package com.pausiar.fit.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pausiar.fit.FitApplication
import com.pausiar.fit.data.repository.RoutineRepository
import com.pausiar.fit.ui.dashboard.DashboardViewModel
import com.pausiar.fit.ui.day.DayViewModel
import com.pausiar.fit.ui.edit.EditRoutineViewModel
import com.pausiar.fit.ui.history.HistoryViewModel
import com.pausiar.fit.ui.settings.SettingsViewModel

/**
 * Central factory wiring the repository into every ViewModel without a DI library.
 */
object AppViewModelFactory {

    private fun CreationExtras.repo(): RoutineRepository {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FitApplication
        return app.repository
    }

    /** Factory for the screens whose ViewModels take no extra arguments. */
    val factory: ViewModelProvider.Factory = viewModelFactory {
        initializer { DashboardViewModel(repo()) }
        initializer { HistoryViewModel(repo()) }
        initializer { EditRoutineViewModel(repo()) }
        initializer { SettingsViewModel(repo()) }
    }

    /** Factory for the day screen, which needs the selected day + date. */
    fun dayFactory(dayId: Int, dateIso: String): ViewModelProvider.Factory =
        viewModelFactory {
            initializer { DayViewModel(repo(), dayId, dateIso) }
        }
}
