package com.artemonre.hireme.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artemonre.hireme.theme.HireMeTheme
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.until

private val monthAbbreviations = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
)

// Fixed placeholder gradient (not theme-derived), same precedent as the brand link gradients.
private val ExperienceCardGradient = Brush.horizontalGradient(listOf(Color(0xFF0F766E), Color(0xFF2DD4BF)))

@Suppress("DEPRECATION")
private fun LocalDate.formatMonthYear(): String = "${monthAbbreviations[monthNumber - 1]} $year"

private fun Experience.formatDateRange(): String {
    val endText = end?.formatMonthYear() ?: "Present"
    return "${start.formatMonthYear()} – $endText"
}

@OptIn(ExperimentalTime::class)
private fun totalExperienceMonths(experience: List<Experience>): Long {
    if (experience.isEmpty()) return 0L
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val earliestStart = experience.minOf { it.start }
    val latestEnd = experience.maxOf { it.end ?: today }
    return earliestStart.until(latestEnd, DateTimeUnit.MONTH)
}

private fun formatDuration(months: Long): String {
    val years = months / 12
    val remainderMonths = months % 12
    return when {
        years <= 0L -> "$remainderMonths mo"
        remainderMonths == 0L -> "$years yr"
        else -> "$years yr $remainderMonths mo"
    }
}

@Composable
fun ExperienceSection(experience: List<Experience>) {
    if (experience.isEmpty()) return

    val uriHandler = LocalUriHandler.current
    val totalMonths = remember(experience) { totalExperienceMonths(experience) }

    Text(
        text = "Experience",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.fillMaxWidth(),
    )
    Text(
        text = "${formatDuration(totalMonths)} total",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(8.dp))
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        experience.forEach { entry ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CardDefaults.shape)
                    .background(ExperienceCardGradient),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(entry.title, style = MaterialTheme.typography.titleSmall, color = Color.White)
                    val employerModifier = if (entry.url != null) {
                        Modifier.clickable { uriHandler.openUri(entry.url) }
                    } else {
                        Modifier
                    }
                    Text(
                        text = entry.employer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                        textDecoration = if (entry.url != null) TextDecoration.Underline else null,
                        modifier = employerModifier,
                    )
                    Text(
                        text = entry.formatDateRange(),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(entry.description, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                }
            }
        }
    }
}

@Composable
@Preview
private fun ExperienceSectionPreview() {
    HireMeTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ExperienceSection(myProfile.experience)
        }
    }
}
