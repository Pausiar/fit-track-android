package com.pausiar.fit.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pausiar.fit.data.repository.RoutineRepository
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: RoutineRepository
) : ViewModel() {

    fun export(onReady: (String) -> Unit) = viewModelScope.launch {
        onReady(repository.exportJson())
    }

    fun import(json: String, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val ok = runCatching { repository.importJson(json) }.isSuccess
        onResult(ok)
    }

    fun resetToday() = viewModelScope.launch { repository.resetDate(com.pausiar.fit.util.DateUtils.toIso(com.pausiar.fit.util.DateUtils.today())) }

    fun resetWeek() = viewModelScope.launch { repository.resetWeek() }

    fun resetAllProgress() = viewModelScope.launch { repository.resetAllProgress() }

    fun resetAllData() = viewModelScope.launch { repository.resetAllData() }
}
