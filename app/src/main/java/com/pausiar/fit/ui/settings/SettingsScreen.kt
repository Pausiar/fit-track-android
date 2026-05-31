package com.pausiar.fit.ui.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pausiar.fit.ui.AppViewModelFactory
import com.pausiar.fit.ui.components.ConfirmDialog
import com.pausiar.fit.ui.theme.Accent
import com.pausiar.fit.ui.theme.AccentSecondary
import com.pausiar.fit.ui.theme.Background
import com.pausiar.fit.ui.theme.BorderColor
import com.pausiar.fit.ui.theme.ErrorAccent
import com.pausiar.fit.ui.theme.Surface
import com.pausiar.fit.ui.theme.TextPrimary
import com.pausiar.fit.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory.factory)
) {
    val context = LocalContext.current
    var pendingExport by remember { mutableStateOf<String?>(null) }
    var confirm by remember { mutableStateOf<Confirm?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        val data = pendingExport
        if (uri != null && data != null) {
            runCatching {
                context.contentResolver.openOutputStream(uri)?.use { it.write(data.toByteArray()) }
            }.onSuccess {
                Toast.makeText(context, "Copia exportada", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(context, "Error al exportar", Toast.LENGTH_SHORT).show()
            }
        }
        pendingExport = null
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val json = runCatching {
                context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            }.getOrNull()
            if (json != null) {
                viewModel.import(json) { ok ->
                    Toast.makeText(
                        context,
                        if (ok) "JSON importado" else "Archivo JSON no válido",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(context, "No se pudo leer el archivo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    confirm?.let { c ->
        ConfirmDialog(
            title = c.title,
            message = c.message,
            confirmLabel = c.confirmLabel,
            destructive = true,
            onConfirm = c.action,
            onDismiss = { confirm = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
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
            Text("Ajustes", style = MaterialTheme.typography.titleLarge)
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionTitle("COPIA DE SEGURIDAD")
            SettingsRow(
                icon = Icons.Default.FileDownload,
                title = "Exportar datos",
                subtitle = "Guarda rutina y progreso en un archivo JSON",
                tint = Accent
            ) {
                viewModel.export { json ->
                    pendingExport = json
                    exportLauncher.launch("jose-luis-fit-backup.json")
                }
            }
            SettingsRow(
                icon = Icons.Default.FileUpload,
                title = "Importar datos",
                subtitle = "Importa una copia o una rutina JSON personalizada",
                tint = AccentSecondary
            ) {
                importLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
            }

            Spacer(Modifier.height(4.dp))
            SectionTitle("RESETEAR")
            SettingsRow(
                icon = Icons.Default.RestartAlt,
                title = "Resetear hoy",
                subtitle = "Desmarca las series de hoy",
                tint = TextSecondary
            ) {
                confirm = Confirm("Resetear hoy", "Se desmarcarán las series de hoy.", "Resetear") {
                    viewModel.resetToday(); confirm = null
                }
            }
            SettingsRow(
                icon = Icons.Default.RestartAlt,
                title = "Resetear semana",
                subtitle = "Borra el progreso de la semana actual",
                tint = TextSecondary
            ) {
                confirm = Confirm("Resetear semana", "Se borrará el progreso de la semana actual.", "Resetear") {
                    viewModel.resetWeek(); confirm = null
                }
            }
            SettingsRow(
                icon = Icons.Default.DeleteForever,
                title = "Borrar todo el progreso",
                subtitle = "Elimina todo el historial (mantiene la rutina)",
                tint = ErrorAccent
            ) {
                confirm = Confirm(
                    "Borrar todo el progreso",
                    "Se eliminará TODO el historial de entrenamientos. Esta acción no se puede deshacer.",
                    "Borrar todo"
                ) { viewModel.resetAllProgress(); confirm = null }
            }
            SettingsRow(
                icon = Icons.Default.DeleteForever,
                title = "Restaurar rutina por defecto",
                subtitle = "Borra todo y restaura la rutina original",
                tint = ErrorAccent
            ) {
                confirm = Confirm(
                    "Restaurar rutina por defecto",
                    "Se borrarán tu rutina personalizada y todo el progreso, volviendo a la rutina original. No se puede deshacer.",
                    "Restaurar"
                ) { viewModel.resetAllData(); confirm = null }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "Jose Luis Fit · 100% offline · v1.0\nModo oscuro permanente · Tus datos nunca salen del dispositivo.",
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

private data class Confirm(
    val title: String,
    val message: String,
    val confirmLabel: String,
    val action: () -> Unit
)

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        color = TextSecondary,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.5.sp,
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(subtitle, color = TextSecondary, fontSize = 12.sp)
        }
    }
}
