package com.pausiar.fit.ui.day

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.StickyNote2
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pausiar.fit.domain.ExerciseProgress
import com.pausiar.fit.ui.AppViewModelFactory
import com.pausiar.fit.ui.components.AppProgressBar
import com.pausiar.fit.ui.components.ConfirmDialog
import com.pausiar.fit.ui.components.TagPill
import com.pausiar.fit.ui.components.focusColor
import com.pausiar.fit.ui.theme.Accent
import com.pausiar.fit.ui.theme.Background
import com.pausiar.fit.ui.theme.BorderColor
import com.pausiar.fit.ui.theme.Surface
import com.pausiar.fit.ui.theme.TextPrimary
import com.pausiar.fit.ui.theme.TextSecondary

@Composable
fun DayScreen(
    dayId: Int,
    dateIso: String,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    viewModel: DayViewModel = viewModel(
        factory = AppViewModelFactory.dayFactory(dayId, dateIso),
        key = "day-$dayId-$dateIso"
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val accent = focusColor(state.focus.ifEmpty { "push" })
    var showResetDay by remember { mutableStateOf(false) }

    if (showResetDay) {
        ConfirmDialog(
            title = "Resetear día",
            message = "Se desmarcarán todas las series de ${state.dayName} para esta fecha. El historial de otras fechas no se toca.",
            confirmLabel = "Resetear",
            onConfirm = { viewModel.resetDay() },
            onDismiss = { showResetDay = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Top bar with sticky progress.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface)
                .padding(top = 44.dp, start = 12.dp, end = 12.dp, bottom = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Volver", tint = TextPrimary)
                }
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(state.dayName, style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.width(8.dp))
                        if (state.focus.isNotEmpty()) TagPill(state.focus, accent)
                    }
                    Text(
                        state.datePretty,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Editar rutina", tint = TextSecondary)
                }
                IconButton(onClick = { showResetDay = true }) {
                    Icon(Icons.Default.Refresh, "Resetear día", tint = TextSecondary)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppProgressBar(
                    fraction = state.progress?.fraction ?: 0f,
                    color = accent,
                    modifier = Modifier.weight(1f),
                    height = 8
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "${state.progress?.percentage ?: 0}%",
                    color = accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.exercises, key = { it.exercise.id }) { item ->
                ExerciseCard(
                    item = item,
                    accent = accent,
                    onToggle = { setIndex -> viewModel.toggleSet(item.exercise.id, setIndex) },
                    onReset = { viewModel.resetExercise(item.exercise.id) },
                    onSaveNote = { note -> viewModel.saveNote(item.exercise, note) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExerciseCard(
    item: ExerciseProgress,
    accent: Color,
    onToggle: (Int) -> Unit,
    onReset: () -> Unit,
    onSaveNote: (String) -> Unit
) {
    val ex = item.exercise
    var noteOpen by remember { mutableStateOf(false) }
    var noteText by remember(ex.id, ex.notes) { mutableStateOf(ex.notes) }

    val borderColor by androidx.compose.animation.animateColorAsState(
        if (item.isComplete) accent.copy(alpha = 0.55f) else BorderColor,
        label = "border"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (item.isComplete) accent.copy(alpha = 0.06f) else Surface)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(ex.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text(
                    ex.muscleGroup,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            Text(
                "${ex.targetSets} × ${ex.targetReps}",
                color = accent,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        Spacer(Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            FlowRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (i in 0 until ex.targetSets) {
                    SetCheckbox(
                        checked = item.completedSetIndices.contains(i),
                        accent = accent,
                        onClick = { onToggle(i) }
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                "${item.completedCount}/${item.totalSets}",
                color = if (item.isComplete) accent else TextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { noteOpen = !noteOpen }) {
                Icon(Icons.Outlined.StickyNote2, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    if (ex.notes.isBlank()) "Añadir nota" else "Nota",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onReset) {
                Icon(Icons.Default.Refresh, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Resetear", color = TextSecondary, fontSize = 12.sp)
            }
        }

        if (ex.notes.isNotBlank() && !noteOpen) {
            Text(
                ex.notes,
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        AnimatedVisibility(visible = noteOpen) {
            Column {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Nota rápida...", color = TextSecondary) },
                    singleLine = false
                )
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = {
                        onSaveNote(noteText)
                        noteOpen = false
                    }) {
                        Text("Guardar nota", color = Accent)
                    }
                }
            }
        }
    }
}

/** Large, satisfying check button with a pop animation. */
@Composable
private fun SetCheckbox(checked: Boolean, accent: Color, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (checked) 1f else 0.92f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = 600f),
        label = "scale"
    )
    val size by animateDpAsState(if (checked) 44.dp else 44.dp, label = "size")
    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(if (checked) accent else Color.Transparent)
            .border(
                width = 1.5.dp,
                color = if (checked) accent else BorderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(Icons.Default.Check, "Completado", tint = Background, modifier = Modifier.size(22.dp))
        }
    }
}
