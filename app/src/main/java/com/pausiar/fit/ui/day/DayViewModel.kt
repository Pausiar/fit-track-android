package com.pausiar.fit.ui.day

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pausiar.fit.data.local.entity.ExerciseEntity
import com.pausiar.fit.data.repository.RoutineRepository
import com.pausiar.fit.domain.DayProgress
import com.pausiar.fit.domain.ExerciseProgress
import com.pausiar.fit.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DayUiState(
    val dayId: Int = 0,
    val dayName: String = "",
    val focus: String = "",
    val dateIso: String = "",
    val datePretty: String = "",
    val exercises: List<ExerciseProgress> = emptyList(),
    val progress: DayProgress? = null,
    val loading: Boolean = true
)

class DayViewModel(
    private val repository: RoutineRepository,
    private val dayId: Int,
    private val dateIso: String
) : ViewModel() {

    val uiState: StateFlow<DayUiState> =
        combine(
            repository.observeDay(dayId),
            repository.observeProgressForDate(dateIso)
        ) { dwe, progressRows ->
            if (dwe == null) {
                DayUiState(dayId = dayId, dateIso = dateIso, loading = false)
            } else {
                val exercises = dwe.orderedExercises.map { ex ->
                    val completed = progressRows
                        .filter { it.exerciseId == ex.id }
                        .map { it.setIndex }
                        .toSet()
                    ExerciseProgress(ex, completed)
                }
                val completedSets = exercises.sumOf { it.completedCount }
                val totalSets = exercises.sumOf { it.totalSets }
                val completedExercises = exercises.count { it.isComplete }

                DayUiState(
                    dayId = dwe.day.id,
                    dayName = dwe.day.name,
                    focus = dwe.day.focus,
                    dateIso = dateIso,
                    datePretty = DateUtils.prettyDate(DateUtils.parse(dateIso)),
                    exercises = exercises,
                    progress = DayProgress(
                        dayId = dwe.day.id,
                        dayName = dwe.day.name,
                        focus = dwe.day.focus,
                        dateIso = dateIso,
                        completedSets = completedSets,
                        totalSets = totalSets,
                        completedExercises = completedExercises,
                        totalExercises = exercises.size
                    ),
                    loading = false
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DayUiState(dayId = dayId, dateIso = dateIso)
        )

    fun toggleSet(exerciseId: Long, setIndex: Int) = viewModelScope.launch {
        repository.toggleSet(exerciseId, dateIso, setIndex)
    }

    fun resetExercise(exerciseId: Long) = viewModelScope.launch {
        repository.resetExercise(exerciseId, dateIso)
    }

    fun resetDay() = viewModelScope.launch {
        repository.resetDate(dateIso)
    }

    fun saveNote(exercise: ExerciseEntity, note: String) = viewModelScope.launch {
        repository.saveNote(exercise, note)
    }
}
