package com.artemonre.hireme.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artemonre.hireme.theme.HireMeTheme

// Only these preset platforms get a brand gradient; anything else falls back to the default chip style.
private fun platformGradient(label: String): Brush? = when (label.lowercase()) {
    "github" -> Brush.horizontalGradient(listOf(Color(0xFF24292F), Color(0xFF57606A)))
    "linkedin" -> Brush.horizontalGradient(listOf(Color(0xFF0A66C2), Color(0xFF2867B2)))
    else -> null
}

@Composable
fun LinksSection(links: List<PortfolioLink>) {
    if (links.isEmpty()) return

    val uriHandler = LocalUriHandler.current

    Text(
        text = "Links",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(8.dp))
    val defaultGradient = Brush.horizontalGradient(
        listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.secondaryContainer),
    )
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        links.forEach { link ->
            val brandGradient = platformGradient(link.label)
            val labelColor = if (brandGradient != null) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
            AssistChip(
                onClick = { uriHandler.openUri(link.url) },
                label = { Text(link.label, color = labelColor) },
                colors = AssistChipDefaults.assistChipColors(containerColor = Color.Transparent),
                border = null,
                modifier = Modifier
                    .clip(AssistChipDefaults.shape)
                    .background(brandGradient ?: defaultGradient),
            )
        }
    }
}

@Composable
@Preview
private fun LinksSectionPreview() {
    HireMeTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            LinksSection(myProfile.links)
        }
    }
}
