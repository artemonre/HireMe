package com.artemonre.hireme.portfolio

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artemonre.hireme.theme.HireMeTheme

@Composable
fun TechnologiesSection(application: List<String>, server: List<String>) {
    if (application.isEmpty() && server.isEmpty()) return

    // Last section on the screen, so it also carries the screen's bottom margin.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ScreenPadding)
            .padding(bottom = ScreenPadding),
    ) {
        Text(
            text = "Technologies used",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        if (application.isNotEmpty()) {
            Text(
                text = "Application side: ${application.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        if (server.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Server side: ${server.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
@Preview
private fun TechnologiesSectionPreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                TechnologiesSection(myProfile.applicationTechnologies, myProfile.serverTechnologies)
            }
        }
    }
}
