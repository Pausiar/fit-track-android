package com.pausiar.fit.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pausiar.fit.domain.DayProgress
import com.pausiar.fit.ui.AppViewModelFactory
import com.pausiar.fit.ui.components.AppProgressBar
import com.pausiar.fit.ui.components.TagPill
import com.pausiar.fit.ui.components.focusColor
import com.pausiar.fit.ui.theme.Accent
import com.pausiar.fit.ui.theme.Background
import com.pausiar.fit.ui.theme.BorderColor
import com.pausiar.fit.ui.theme.Surface
import com.pausiar.fit.ui.theme.SurfaceVariant
import com.pausiar.fit.ui.theme.TextPrimary
import com.pausiar.fit.ui.theme.TextSecondary

@Composable
fun DashboardScreen(
    onOpenDay: (dayId: Int, dateIso: String) -> Unit,
    onOpenHistory: () -> Unit,
    onOpenEdit: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: DashboardViewModel = viewModel(factory = AppViewModelFactory.factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 56.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "JOSE LUIS FIT",
                        color = Accent,
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        letterSpacing = 2.sp
                    )
                    Text(
                        state.todayPretty,
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                IconButton(onClick = onOpenHistory) {
                    Icon(Icons.Default.History, "Historial", tint = TextPrimary)
                }
                IconButton(onClick = onOpenEdit) {
                    Icon(Icons.Default.Edit, "Editar rutina", tint = TextPrimary)
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(Icons.Default.Settings, "Ajustes", tint = TextPrimary)
                }
            }
        }

        item {
            TodayHeroCard(
                state = state,
                onOpenDay = { onOpenDay(state.todayDayId, state.todayDateIso) }
            )
        }

        item {
            Text(
                "TU SEMANA",
                color = TextSecondary,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(state.week, key = { it.dayId }) { day ->
            DayCard(
                day = day,
                highlightToday = day.dayId == state.todayDayId,
                onClick = { onOpenDay(day.dayId, day.dateIso) }
            )
        }
    }
}

@Composable
private fun TodayHeroCard(state: DashboardUiState, onOpenDay: () -> Unit) {
    val progress = state.todayProgress
    val accent = focusColor(state.todayFocus.ifEmpty { "push" })
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceVariant)
            .border(1.dp, BorderColor, RoundedCornerShape(18.dp))
            .clickable(onClick = onOpenDay)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "HOY",
                color = accent,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.width(8.dp))
            if (state.todayFocus.isNotEmpty()) TagPill(state.todayFocus, accent)
        }
        Spacer(Modifier.height(6.dp))
        Text(
            state.todayName.ifEmpty { "—" },
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary
        )
        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            HeroStat("${progress?.percentage ?: 0}%", "Progreso", accent, Modifier.weight(1f))
            HeroStat(
                "${progress?.completedExercises ?: 0}/${progress?.totalExercises ?: 0}",
                "Ejercicios", TextPrimary, Modifier.weight(1f)
            )
            HeroStat(
                "${progress?.completedSets ?: 0}/${progress?.totalSets ?: 0}",
                "Series", TextPrimary, Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(16.dp))
        AppProgressBar(fraction = progress?.fraction ?: 0f, color = accent, height = 8)
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(accent)
                .clickable(onClick = onOpenDay)
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.PlayArrow, null, tint = Background)
            Spacer(Modifier.width(6.dp))
            Text("Entrenar hoy", color = Background, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun HeroStat(value: String, label: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Column(modifier) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 26.sp)
        Text(label, color = TextSecondary, fontSize = 11.sp, letterSpacing = 0.5.sp)
    }
}

@Composable
private fun DayCard(day: DayProgress, highlightToday: Boolean, onClick: () -> Unit) {
    val accent = focusColor(day.focus)
    val isRest = day.focus.equals("Descanso", ignoreCase = true)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .border(
                width = if (highlightToday) 1.5.dp else 1.dp,
                color = if (highlightToday) accent.copy(alpha = 0.5f) else BorderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(accent)
            )
            Spacer(Modifier.width(10.dp))
            Text(day.dayName, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Spacer(Modifier.weight(1f))
            TagPill(day.focus, accent)
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppProgressBar(
                fraction = day.fraction,
                color = accent,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                if (isRest) "${day.completedSets}/${day.totalSets}" else "${day.completedSets}/${day.totalSets} series",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
