package com.artemonre.hireme.portfolio

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artemonre.hireme.theme.HireMeTheme

@Composable
fun ContactsSection(contacts: List<PortfolioLink>) {
    if (contacts.isEmpty()) return

    val uriHandler = LocalUriHandler.current

    Text(
        text = "You can reach me by one of those options:",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(8.dp))
    contacts.forEach { contact ->
        Text(
            text = contact.label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { uriHandler.openUri(contact.url) }
                .padding(vertical = 8.dp),
        )
    }
}

@Composable
@Preview
private fun ContactsSectionPreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                ContactsSection(myProfile.contacts)
            }
        }
    }
}
