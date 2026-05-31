package com.pausiar.fit.ui.edit

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pausiar.fit.data.local.entity.ExerciseEntity
import com.pausiar.fit.data.local.relation.DayWithExercises
import com.pausiar.fit.ui.AppViewModelFactory
import com.pausiar.fit.ui.components.ConfirmDialog
import com.pausiar.fit.ui.components.TagPill
import com.pausiar.fit.ui.components.focusColor
import com.pausiar.fit.ui.theme.Accent
import com.pausiar.fit.ui.theme.Background
import com.pausiar.fit.ui.theme.BorderColor
import com.pausiar.fit.ui.theme.ErrorAccent
import com.pausiar.fit.ui.theme.Surface
import com.pausiar.fit.ui.theme.SurfaceVariant
import com.pausiar.fit.ui.theme.TextPrimary
import com.pausiar.fit.ui.theme.TextSecondary

@Composable
fun EditRoutineScreen(
    onBack: () -> Unit,
    viewModel: EditRoutineViewModel = viewModel(factory = AppViewModelFactory.factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var editing by remember { mutableStateOf<ExerciseEntity?>(null) }
    var addingForDay by remember { mutableStateOf<Int?>(null) }
    var deleting by remember { mutableStateOf<ExerciseEntity?>(null) }

    editing?.let { ex ->
        ExerciseDialog(
            initial = ex,
            onDismiss = { editing = null },
            onSave = { name, muscle, sets, reps ->
                viewModel.updateExercise(
                    ex.copy(name = name, muscleGroup = muscle, targetSets = sets, targetReps = reps)
                )
                editing = null
            }
        )
    }

    addingForDay?.let { dayId ->
        ExerciseDialog(
            initial = null,
            onDismiss = { addingForDay = null },
            onSave = { name, muscle, sets, reps ->
                viewModel.addExercise(dayId, name, muscle, sets, reps)
                addingForDay = null
            }
        )
    }

    deleting?.let { ex ->
        ConfirmDialog(
            title = "Eliminar ejercicio",
            message = "¿Eliminar \"${ex.name}\"? El progreso asociado también se borrará.",
            confirmLabel = "Eliminar",
            onConfirm = { viewModel.deleteExercise(ex) },
            onDismiss = { deleting = null }
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
            Text("Editar rutina", style = MaterialTheme.typography.titleLarge)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(state.days, key = { it.day.id }) { dwe ->
                DayEditCard(
                    dwe = dwe,
                    onAdd = { addingForDay = dwe.day.id },
                    onEdit = { editing = it },
                    onDelete = { deleting = it }
                )
            }
        }
    }
}

@Composable
private fun DayEditCard(
    dwe: DayWithExercises,
    onAdd: () -> Unit,
    onEdit: (ExerciseEntity) -> Unit,
    onDelete: (ExerciseEntity) -> Unit
) {
    val accent = focusColor(dwe.day.focus.ifEmpty { "push" })
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(dwe.day.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Spacer(Modifier.width(8.dp))
            if (dwe.day.focus.isNotEmpty()) TagPill(dwe.day.focus, accent)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onAdd) {
                Icon(Icons.Default.Add, "Añadir ejercicio", tint = accent)
            }
        }
        Spacer(Modifier.height(6.dp))
        dwe.orderedExercises.forEach { ex ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(ex.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(
                        "${ex.targetSets} × ${ex.targetReps}" +
                            if (ex.muscleGroup.isNotBlank()) " · ${ex.muscleGroup}" else "",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                IconButton(onClick = { onEdit(ex) }) {
                    Icon(Icons.Default.Edit, "Editar", tint = TextSecondary, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = { onDelete(ex) }) {
                    Icon(Icons.Default.Delete, "Eliminar", tint = ErrorAccent, modifier = Modifier.size(18.dp))
                }
            }
        }
        if (dwe.exercises.isEmpty()) {
            Text("Sin ejercicios. Pulsa + para añadir.", color = TextSecondary, fontSize = 13.sp)
        }
    }
}

@Composable
private fun ExerciseDialog(
    initial: ExerciseEntity?,
    onDismiss: () -> Unit,
    onSave: (name: String, muscle: String, sets: Int, reps: String) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var muscle by remember { mutableStateOf(initial?.muscleGroup ?: "") }
    var sets by remember { mutableStateOf((initial?.targetSets ?: 3).toString()) }
    var reps by remember { mutableStateOf(initial?.targetReps ?: "10") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceVariant,
        title = { Text(if (initial == null) "Nuevo ejercicio" else "Editar ejercicio", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = muscle,
                    onValueChange = { muscle = it },
                    label = { Text("Grupo muscular") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = sets,
                        onValueChange = { v -> sets = v.filter { it.isDigit() }.take(2) },
                        label = { Text("Series") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = reps,
                        onValueChange = { reps = it },
                        label = { Text("Reps") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val s = sets.toIntOrNull()?.coerceAtLeast(1) ?: 1
                    if (name.isNotBlank()) onSave(name.trim(), muscle.trim(), s, reps.trim())
                },
                enabled = name.isNotBlank()
            ) {
                Text("Guardar", color = Accent, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextSecondary) }
        }
    )
}
