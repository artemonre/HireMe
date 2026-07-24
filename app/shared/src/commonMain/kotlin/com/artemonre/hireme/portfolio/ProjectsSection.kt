package com.artemonre.hireme.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artemonre.hireme.theme.HireMeTheme

// Below this width, a card fills the available width (one at a time, like a mobile carousel).
// At or above it, a card takes a fraction of the width, floored so it never gets cramped
// even if the viewport is only barely past the breakpoint.
private val MinWideCardWidth = 280.dp
private const val WideCardWidthFraction = 0.3f

@Composable
fun ProjectsSection(projects: List<Project>) {
    if (projects.isEmpty()) return

    // A solid color paired with its guaranteed "on" role, rather than a gradient between
    // two roles (e.g. primary + primaryContainer) whose relative lightness isn't
    // guaranteed to stay consistent across light/dark themes or future palette edits.
    val cardColor = MaterialTheme.colorScheme.primary
    val cardTextColor = MaterialTheme.colorScheme.onPrimary

    Text(
        text = "Projects",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.fillMaxWidth().padding(horizontal = ScreenPadding),
    )
    Spacer(Modifier.height(8.dp))
    // No horizontal padding on the row itself (unlike other sections) — it spans the full
    // screen width and uses contentPadding instead, so a scrolled-to card can peek past the
    // last full card right up to the screen edge instead of stopping dead at a hard margin.
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val usableWidth = (maxWidth - ScreenPadding * 2).coerceAtLeast(0.dp)
        val cardWidth = if (maxWidth < WideLayoutBreakpoint) {
            usableWidth
        } else {
            (usableWidth * WideCardWidthFraction).coerceIn(MinWideCardWidth, usableWidth)
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = ScreenPadding),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(projects) { project ->
                Card(
                    modifier = Modifier
                        .width(cardWidth)
                        .clip(CardDefaults.shape)
                        .background(cardColor),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(project.name, style = MaterialTheme.typography.titleSmall, color = cardTextColor)
                        Text(
                            text = project.duration,
                            style = MaterialTheme.typography.bodySmall,
                            color = cardTextColor.copy(alpha = 0.85f),
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(project.description, style = MaterialTheme.typography.bodyMedium, color = cardTextColor)
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun ProjectsSectionPreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProjectsSection(myProfile.projects)
            }
        }
    }
}
