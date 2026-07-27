package com.artemonre.hireme.portfolio

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artemonre.hireme.components.BoardgameCardSurface
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

private enum class ExperienceExpansion { COLLAPSED, HALF_EXPANDED, EXPANDED }

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
    // A solid color paired with its guaranteed "on" role, rather than a gradient between
    // two roles (e.g. secondary + secondaryContainer) whose relative lightness isn't
    // guaranteed to stay consistent across light/dark themes or future palette edits.
    val cardColor = MaterialTheme.colorScheme.secondary
    val cardTextColor = MaterialTheme.colorScheme.onSecondary
    var expansion by remember { mutableStateOf(ExperienceExpansion.COLLAPSED) }
    var showBottomSheet by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isWideScreen = maxWidth >= WideLayoutBreakpoint

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = ScreenPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isWideScreen) {
                            expansion = if (expansion == ExperienceExpansion.COLLAPSED) {
                                ExperienceExpansion.HALF_EXPANDED
                            } else {
                                ExperienceExpansion.COLLAPSED
                            }
                        } else {
                            showBottomSheet = true
                        }
                    },
            ) {
                Text(
                    text = "Experience",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${formatDuration(totalMonths)} total",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (isWideScreen) {
                        Spacer(Modifier.width(4.dp))
                        val rotation by animateFloatAsState(
                            if (expansion != ExperienceExpansion.COLLAPSED) 180f else 0f,
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp).rotate(rotation),
                        )
                    }
                }
            }

            if (isWideScreen && expansion != ExperienceExpansion.COLLAPSED) {
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth().animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    experience.forEach { entry ->
                        ExperienceCard(
                            entry = entry,
                            cardColor = cardColor,
                            cardTextColor = cardTextColor,
                            showFullDetail = expansion == ExperienceExpansion.EXPANDED,
                            onClick = if (expansion == ExperienceExpansion.HALF_EXPANDED) {
                                { expansion = ExperienceExpansion.EXPANDED }
                            } else {
                                null
                            },
                            uriHandler = uriHandler,
                        )
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        PortfolioModal(onDismissRequest = { showBottomSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                experience.forEach { entry ->
                    ExperienceCard(
                        entry = entry,
                        cardColor = cardColor,
                        cardTextColor = cardTextColor,
                        showFullDetail = true,
                        onClick = null,
                        uriHandler = uriHandler,
                    )
                }
            }
        }
    }
}

@Composable
private fun ExperienceCard(
    entry: Experience,
    cardColor: Color,
    cardTextColor: Color,
    showFullDetail: Boolean,
    onClick: (() -> Unit)?,
    uriHandler: UriHandler,
    modifier: Modifier = Modifier,
) {
    BoardgameCardSurface(
        shape = CardDefaults.shape,
        backgroundColor = cardColor,
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(entry.title, style = MaterialTheme.typography.titleSmall, color = cardTextColor)
            val employerModifier = if (showFullDetail && entry.url != null) {
                Modifier.clickable { uriHandler.openUri(entry.url) }
            } else {
                Modifier
            }
            Text(
                text = entry.employer,
                style = MaterialTheme.typography.bodyMedium,
                color = cardTextColor.copy(alpha = 0.85f),
                textDecoration = if (showFullDetail && entry.url != null) TextDecoration.Underline else null,
                modifier = employerModifier,
            )
            Text(
                text = entry.formatDateRange(),
                style = MaterialTheme.typography.bodySmall,
                color = cardTextColor.copy(alpha = 0.85f),
            )
            if (showFullDetail) {
                Spacer(Modifier.height(8.dp))
                Text(entry.description, style = MaterialTheme.typography.bodyMedium, color = cardTextColor)
            }
        }
    }
}

@Composable
@Preview
private fun ExperienceSectionPreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                ExperienceSection(myProfile.experience)
            }
        }
    }
}
