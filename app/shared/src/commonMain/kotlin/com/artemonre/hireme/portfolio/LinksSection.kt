package com.artemonre.hireme.portfolio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artemonre.hireme.components.BoardgameChip
import com.artemonre.hireme.theme.HireMeTheme

// Only these preset platforms get a brand color; anything else falls back to the default chip style.
private fun platformColor(label: String): Color? = when (label.lowercase()) {
    "github" -> Color(0xFF24292F)
    "linkedin" -> Color(0xFF0A66C2)
    else -> null
}

@Composable
fun LinksSection(links: List<PortfolioLink>) {
    if (links.isEmpty()) return

    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ScreenPadding),
    ) {
        Text(
            text = "Links",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            links.forEach { link ->
                val brandColor = platformColor(link.label)
                BoardgameChip(
                    text = link.label,
                    backgroundColor = brandColor ?: MaterialTheme.colorScheme.primaryContainer,
                    textColor = if (brandColor != null) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.clickable { uriHandler.openUri(link.url) },
                )
            }
        }
    }
}

@Composable
@Preview
private fun LinksSectionPreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                LinksSection(myProfile.links)
            }
        }
    }
}
