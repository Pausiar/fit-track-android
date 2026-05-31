package com.pausiar.fit.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pausiar.fit.domain.HistoryEntry
import com.pausiar.fit.ui.AppViewModelFactory
import com.pausiar.fit.ui.components.AppProgressBar
import com.pausiar.fit.ui.components.ConfirmDialog
import com.pausiar.fit.ui.components.EmptyState
import com.pausiar.fit.ui.components.TagPill
import com.pausiar.fit.ui.components.focusColor
import com.pausiar.fit.ui.theme.Accent
import com.pausiar.fit.ui.theme.AccentSecondary
import com.pausiar.fit.ui.theme.Background
import com.pausiar.fit.ui.theme.BorderColor
import com.pausiar.fit.ui.theme.ErrorAccent
import com.pausiar.fit.ui.theme.Surface
import com.pausiar.fit.ui.theme.SurfaceVariant
import com.pausiar.fit.ui.theme.TextPrimary
import com.pausiar.fit.ui.theme.TextSecondary
import com.pausiar.fit.util.DateUtils

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = viewModel(factory = AppViewModelFactory.factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var confirm by remember { mutableStateOf<Confirm?>(null) }

    confirm?.let { c ->
        ConfirmDialog(
            title = c.title,
            message = c.message,
            confirmLabel = "Resetear",
            onConfirm = c.action,
            onDismiss = { confirm = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 44.dp, start = 12.dp, end = 12.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Volver", tint = TextPrimary)
            }
            Text("Historial", style = MaterialTheme.typography.titleLarge)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatBox("${state.stats.currentStreak}", "Racha actual", Accent, Modifier.weight(1f), fire = true)
                    StatBox("${state.stats.bestStreak}", "Mejor racha", AccentSecondary, Modifier.weight(1f))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatBox("${state.stats.completedDays}", "Días entrenados", TextPrimary, Modifier.weight(1f))
                    StatBox("${state.stats.totalCompletedSets}", "Series totales", TextPrimary, Modifier.weight(1f))
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ResetButton(
                        "Resetear hoy",
                        Modifier.weight(1f)
                    ) {
                        confirm = Confirm(
                            "Resetear hoy",
                            "Se desmarcarán todas las series de hoy. El historial de otros días no se toca.",
                            { viewModel.resetToday(); confirm = null }
                        )
                    }
                    ResetButton(
                        "Resetear semana",
                        Modifier.weight(1f)
                    ) {
                        confirm = Confirm(
                            "Resetear semana",
                            "Se borrará el progreso de toda la semana actual (Lun–Dom). Otras semanas se mantienen.",
                            { viewModel.resetWeek(); confirm = null }
                        )
                    }
                }
            }

            item {
                Text(
                    "POR FECHA",
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (state.entries.isEmpty() && !state.loading) {
                item {
                    EmptyState(
                        icon = Icons.Outlined.Inbox,
                        title = "Sin entrenamientos aún",
                        subtitle = "Marca tus primeras series para empezar tu historial."
                    )
                }
            }

            items(state.entries, key = { it.dateIso }) { entry ->
                HistoryRow(entry)
            }
        }
    }
}

private data class Confirm(val title: String, val message: String, val action: () -> Unit)

@Composable
private fun StatBox(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier,
    fire: Boolean = false
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceVariant)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 30.sp)
            if (fire && value != "0") {
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Default.LocalFireDepartment, null, tint = color, modifier = Modifier)
            }
        }
        Text(label, color = TextSecondary, fontSize = 12.sp)
    }
}

@Composable
private fun ResetButton(text: String, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, ErrorAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .background(ErrorAccent.copy(alpha = 0.06f)),
        contentAlignment = Alignment.Center
    ) {
        TextButton(onClick = onClick) {
            Text(text, color = ErrorAccent, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun HistoryRow(entry: HistoryEntry) {
    val accent = focusColor(entry.focus.ifEmpty { "push" })
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    DateUtils.shortDate(DateUtils.parse(entry.dateIso)),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Text("${entry.dayName} · ${entry.completedSets}/${entry.totalSets} series", color = TextSecondary, fontSize = 12.sp)
            }
            if (entry.focus.isNotEmpty()) TagPill(entry.focus, accent)
            Spacer(Modifier.width(10.dp))
            Text("${entry.percentage}%", color = accent, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        AppProgressBar(fraction = entry.completedSets.toFloat() / entry.totalSets.coerceAtLeast(1), color = accent)
    }
}
