package com.pausiar.fit.ui.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pausiar.fit.domain.HistoryEntry
import com.pausiar.fit.ui.theme.Accent
import com.pausiar.fit.ui.theme.BorderColor
import com.pausiar.fit.ui.theme.Surface
import com.pausiar.fit.ui.theme.SurfaceVariant
import com.pausiar.fit.ui.theme.TextPrimary
import com.pausiar.fit.ui.theme.TextSecondary
import com.pausiar.fit.util.DateUtils

private const val WEEKS = 53

private val MonthLabels = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")
private val WeekdayLabels = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")

private val LevelColors = listOf(
    SurfaceVariant,
    Accent.copy(alpha = 0.25f),
    Accent.copy(alpha = 0.45f),
    Accent.copy(alpha = 0.7f),
    Accent
)

@Composable
fun ActivityHeatmapCard(entries: List<HistoryEntry>, modifier: Modifier = Modifier) {
    val activity = remember(entries) { entries.associate { it.dateIso to it.completedSets } }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Outlined.CalendarMonth, null, tint = Accent)
            Text(
                "Actividad de entrenamiento",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
        }
        Spacer(Modifier.height(14.dp))
        ActivityHeatmap(activity)
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Menos", color = TextSecondary, fontSize = 11.sp)
            Spacer(Modifier.width(6.dp))
            LevelColors.forEach { color ->
                Box(Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(color))
                Spacer(Modifier.width(3.dp))
            }
            Text("Más", color = TextSecondary, fontSize = 11.sp)
        }
    }
}

@Composable
private fun ActivityHeatmap(activity: Map<String, Int>) {
    val today = DateUtils.today()
    val start = remember(today) { DateUtils.startOfWeek(today).minusWeeks((WEEKS - 1).toLong()) }
    val maxSets = remember(activity, start, today) {
        var max = 0
        var date = start
        while (!date.isAfter(today)) {
            max = maxOf(max, activity[DateUtils.toIso(date)] ?: 0)
            date = date.plusDays(1)
        }
        max
    }

    val measurer = rememberTextMeasurer()
    val labelStyle = TextStyle(color = TextSecondary, fontSize = 9.sp)
    val monthTexts = remember(measurer) {
        MonthLabels.map { measurer.measure(AnnotatedString(it), style = labelStyle) }
    }
    val weekdayTexts = remember(measurer) {
        WeekdayLabels.map { measurer.measure(AnnotatedString(it), style = labelStyle) }
    }

    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val labelWidth = 26.dp
        val topHeight = 16.dp
        val step = (maxWidth - labelWidth) / WEEKS
        val gap = 2.dp.coerceAtMost(step / 3)
        val cell = step - gap
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(topHeight + step * 7)
                .semantics {
                    contentDescription = "Mapa de actividad de entrenamiento del último año"
                }
        ) {
            val labelPx = labelWidth.toPx()
            val topPx = topHeight.toPx()
            val stepPx = step.toPx()
            val cellPx = cell.toPx()
            val corner = CornerRadius((cell * 0.25f).coerceAtMost(2.dp).toPx())

            weekdayTexts.forEachIndexed { row, text ->
                if (row % 2 == 0) {
                    drawText(
                        text,
                        topLeft = Offset(0f, topPx + row * stepPx + (cellPx - text.size.height) / 2f)
                    )
                }
            }

            var previousMonth = -1
            for (week in 0 until WEEKS) {
                val monday = start.plusWeeks(week.toLong())
                val month = monday.monthValue - 1
                if (month != previousMonth) {
                    previousMonth = month
                    if (week < WEEKS - 2) {
                        drawText(monthTexts[month], topLeft = Offset(labelPx + week * stepPx, 0f))
                    }
                }
                for (row in 0..6) {
                    val date = monday.plusDays(row.toLong())
                    val count = if (date.isAfter(today)) 0 else activity[DateUtils.toIso(date)] ?: 0
                    val level = if (count <= 0 || maxSets <= 0) 0 else ((count * 4 + maxSets - 1) / maxSets).coerceIn(1, 4)
                    drawRoundRect(
                        color = LevelColors[level],
                        topLeft = Offset(labelPx + week * stepPx, topPx + row * stepPx),
                        size = Size(cellPx, cellPx),
                        cornerRadius = corner
                    )
                }
            }
        }
    }
}