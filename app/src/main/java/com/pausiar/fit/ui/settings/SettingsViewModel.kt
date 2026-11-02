package com.pausiar.fit.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pausiar.fit.data.repository.RoutineRepository
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: RoutineRepository
) : ViewModel() {

    // Guardado en el ViewModel (no en la pantalla) para que sobreviva a la recreación
    // de la Activity mientras el selector de archivos está abierto.
    private var pendingExport: String? = null

    fun export(onReady: () -> Unit) = viewModelScope.launch {
        pendingExport = repository.exportJson()
        onReady()
    }

    fun takePendingExport(): String? = pendingExport.also { pendingExport = null }

    fun import(json: String, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val ok = runCatching { repository.importJson(json) }.isSuccess
        onResult(ok)
    }

    fun resetToday() = viewModelScope.launch { repository.resetDate(com.pausiar.fit.util.DateUtils.toIso(com.pausiar.fit.util.DateUtils.today())) }

    fun resetWeek() = viewModelScope.launch { repository.resetWeek() }

    fun resetAllProgress() = viewModelScope.launch { repository.resetAllProgress() }

    fun resetAllData() = viewModelScope.launch { repository.resetAllData() }
}
