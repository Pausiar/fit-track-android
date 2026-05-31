package com.pausiar.fit.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pausiar.fit.data.repository.RoutineRepository
import com.pausiar.fit.domain.DayProgress
import com.pausiar.fit.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val todayPretty: String = "",
    val todayDayId: Int = 1,
    val todayName: String = "",
    val todayFocus: String = "",
    val todayDateIso: String = "",
    val todayProgress: DayProgress? = null,
    val week: List<DayProgress> = emptyList(),
    val loading: Boolean = true
)

class DashboardViewModel(
    private val repository: RoutineRepository
) : ViewModel() {

    init {
        // Seed the default routine on first launch.
        viewModelScope.launch { repository.ensureSeeded() }
    }

    private val today = DateUtils.today()
    private val weekStart = DateUtils.toIso(DateUtils.startOfWeek(today))
    private val weekEnd = DateUtils.toIso(DateUtils.endOfWeek(today))

    val uiState: StateFlow<DashboardUiState> =
        combine(
            repository.observeDaysWithExercises(),
            repository.observeProgressBetween(weekStart, weekEnd)
        ) { days, weekProgress ->
            val todayDow = DateUtils.isoDayOfWeek(today)

            val week = days.map { dwe ->
                val date = DateUtils.dateForDayOfWeek(dwe.day.dayOfWeek, today)
                val dateIso = DateUtils.toIso(date)
                val exerciseIds = dwe.exercises.associateBy { it.id }
                val rows = weekProgress.filter { it.date == dateIso && exerciseIds.containsKey(it.exerciseId) }

                var completedSets = 0
                var completedExercises = 0
                dwe.exercises.forEach { ex ->
                    val done = rows.count { it.exerciseId == ex.id && it.setIndex < ex.targetSets }
                    completedSets += done
                    if (ex.targetSets > 0 && done >= ex.targetSets) completedExercises++
                }

                DayProgress(
                    dayId = dwe.day.id,
                    dayName = dwe.day.name,
                    focus = dwe.day.focus,
                    dateIso = dateIso,
                    completedSets = completedSets,
                    totalSets = dwe.totalSets,
                    completedExercises = completedExercises,
                    totalExercises = dwe.exercises.size
                )
            }

            val todayProgress = week.firstOrNull { it.dayId == todayDow }

            DashboardUiState(
                todayPretty = DateUtils.prettyDate(today),
                todayDayId = todayDow,
                todayName = todayProgress?.dayName ?: "",
                todayFocus = todayProgress?.focus ?: "",
                todayDateIso = todayProgress?.dateIso ?: DateUtils.toIso(today),
                todayProgress = todayProgress,
                week = week,
                loading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState()
        )
}
