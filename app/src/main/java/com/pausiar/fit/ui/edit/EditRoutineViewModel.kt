package com.pausiar.fit.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pausiar.fit.data.local.entity.ExerciseEntity
import com.pausiar.fit.data.local.relation.DayWithExercises
import com.pausiar.fit.data.repository.RoutineRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class EditUiState(
    val days: List<DayWithExercises> = emptyList(),
    val loading: Boolean = true
)

class EditRoutineViewModel(
    private val repository: RoutineRepository
) : ViewModel() {

    val uiState: StateFlow<EditUiState> =
        repository.observeDaysWithExercises()
            .map { EditUiState(days = it, loading = false) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = EditUiState()
            )

    fun addExercise(
        dayId: Int,
        name: String,
        muscleGroup: String,
        targetSets: Int,
        targetReps: String
    ) = viewModelScope.launch {
        repository.addExercise(dayId, name.trim(), muscleGroup.trim(), targetSets, targetReps.trim())
    }

    fun updateExercise(exercise: ExerciseEntity) = viewModelScope.launch {
        repository.updateExercise(exercise)
    }

    fun deleteExercise(exercise: ExerciseEntity) = viewModelScope.launch {
        repository.deleteExercise(exercise)
    }
}
