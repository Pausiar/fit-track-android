package com.pausiar.fit.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pausiar.fit.ui.theme.Accent
import com.pausiar.fit.ui.theme.AccentSecondary
import com.pausiar.fit.ui.theme.BorderColor
import com.pausiar.fit.ui.theme.ErrorAccent
import com.pausiar.fit.ui.theme.FullBodyColor
import com.pausiar.fit.ui.theme.LegsColor
import com.pausiar.fit.ui.theme.PullColor
import com.pausiar.fit.ui.theme.RestColor
import com.pausiar.fit.ui.theme.TextSecondary

/** Maps a focus string to its accent color. */
fun focusColor(focus: String): Color {
    val normalized = focus.lowercase()
    return when {
        "descanso" in normalized -> RestColor
        "empuje" in normalized && ("tirón" in normalized || "tiron" in normalized) -> AccentSecondary
        "push" in normalized || "empuje" in normalized -> Accent
        "pull" in normalized || "tirón" in normalized || "tiron" in normalized -> PullColor
        "pierna" in normalized || "legs" in normalized || "potencia" in normalized -> LegsColor
        "movilidad" in normalized || "core" in normalized -> AccentSecondary
        "estabilidad" in normalized || "full body" in normalized -> FullBodyColor
        else -> Accent
    }
}

/** Slim animated progress bar with a configurable color. */
@Composable
fun AppProgressBar(
    fraction: Float,
    modifier: Modifier = Modifier,
    color: Color = Accent,
    height: Int = 6
) {
    val animated by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = tween(450),
        label = "progress"
    )
    Box(
        modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(50))
            .background(BorderColor)
    ) {
        Box(
            Modifier
                .fillMaxWidth(animated)
                .height(height.dp)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
    }
}

/** Small monospace-ish pill label. */
@Composable
fun TagPill(text: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text.uppercase(),
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
    }
}

/** Centered empty-state with icon, title and subtitle. */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

/** Reusable destructive confirmation dialog. */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String = "Confirmar",
    destructive: Boolean = true,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message, color = TextSecondary) },
        confirmButton = {
            TextButton(onClick = { onConfirm(); onDismiss() }) {
                Text(confirmLabel, color = if (destructive) ErrorAccent else Accent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextSecondary) }
        }
    )
}

/** Tiny section header used inside screens. */
@Composable
fun SectionLabel(number: String, title: String) {
    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            number,
            color = Accent,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(end = 8.dp, bottom = 4.dp)
        )
        Text(title, style = MaterialTheme.typography.headlineMedium)
    }
}

internal val ZeroPadding = PaddingValues(0.dp)
