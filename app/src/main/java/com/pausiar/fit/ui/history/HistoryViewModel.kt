package com.pausiar.fit.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pausiar.fit.data.repository.RoutineRepository
import com.pausiar.fit.domain.HistoryEntry
import com.pausiar.fit.domain.HistoryStats
import com.pausiar.fit.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HistoryUiState(
    val stats: HistoryStats = HistoryStats(0, 0, 0, 0),
    val entries: List<HistoryEntry> = emptyList(),
    val loading: Boolean = true
)

class HistoryViewModel(
    private val repository: RoutineRepository
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> =
        combine(
            repository.observeAllProgress(),
            repository.observeDaysWithExercises()
        ) { progress, days ->
            val templatesByDow = days.associateBy { it.day.dayOfWeek }

            val entries = progress
                .groupBy { it.date }
                .map { (dateIso, rows) ->
                    val dow = DateUtils.parse(dateIso).dayOfWeek.value
                    val template = templatesByDow[dow]
                    HistoryEntry(
                        dateIso = dateIso,
                        dayName = template?.day?.name ?: dateIso,
                        focus = template?.day?.focus ?: "",
                        completedSets = rows.size,
                        totalSets = template?.totalSets ?: rows.size
                    )
                }
                .sortedByDescending { it.dateIso }

            HistoryUiState(
                stats = repository.buildStats(progress),
                entries = entries,
                loading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HistoryUiState()
        )

    fun resetToday() = viewModelScope.launch {
        repository.resetDate(DateUtils.toIso(DateUtils.today()))
    }

    fun resetWeek() = viewModelScope.launch {
        repository.resetWeek()
    }
}
