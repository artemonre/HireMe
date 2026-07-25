package com.artemonre.hireme.portfolio

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artemonre.hireme.theme.HireMeTheme
import kotlinx.coroutines.launch

private const val ContactsFullTitle = "You can reach me by one of these options:"
private val ContactsSheetTitleHorizontalPadding = 64.dp

@Composable
fun ContactsSection(contacts: List<PortfolioContact>, isWideScreen: Boolean, snackbarHostState: SnackbarHostState) {
    if (contacts.isEmpty()) return

    val uriHandler = LocalUriHandler.current
    var showContactsList by remember { mutableStateOf(false) }

    Column(
        modifier = if (isWideScreen) {
            Modifier.fillMaxWidth()
        } else {
            Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenPadding)
        },
    ) {
        Text(
            text = if (isWideScreen) ContactsFullTitle else "Contacts",
            style = MaterialTheme.typography.titleMedium,
            modifier = if (isWideScreen) Modifier else Modifier.clickable { showContactsList = true },
        )
        // On wide screens contacts sit right here, always visible. On narrow screens, tapping
        // the header above opens a bottom sheet instead (same pattern as ExperienceSection).
        if (isWideScreen) {
            Spacer(Modifier.height(8.dp))
            contacts.forEach { contact -> ContactLink(contact, uriHandler, snackbarHostState) }
        }
    }

    if (!isWideScreen && showContactsList) {
        PortfolioModal(onDismissRequest = { showContactsList = false }) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = ContactsFullTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = ContactsSheetTitleHorizontalPadding),
                )
                Spacer(Modifier.height(8.dp))
                Column(modifier = Modifier.padding(horizontal = ScreenPadding)) {
                    contacts.forEach { contact -> ContactLink(contact, uriHandler, snackbarHostState) }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ContactLink(contact: PortfolioContact, uriHandler: UriHandler, snackbarHostState: SnackbarHostState) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val copyToClipboard: () -> Unit = {
        scope.launch {
            clipboard.setClipEntry(plainTextClipEntry(contact.value))
            snackbarHostState.showSnackbar("Copied ${contact.label} to clipboard")
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .copyOnSecondaryClick(onCopy = copyToClipboard)
            .combinedClickable(
                onClick = { uriHandler.openUri(contact.url) },
                onLongClick = copyToClipboard,
            )
    ) {
        Text(
            text = "${contact.label}: ",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = contact.value,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(Modifier.width(4.dp))
        ContactCopyButton(onCopy = copyToClipboard)
    }
}

@Composable
@Preview
private fun ContactsSectionWidePreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                ContactsSection(myProfile.contacts, isWideScreen = true, snackbarHostState = remember { SnackbarHostState() })
            }
        }
    }
}

@Composable
@Preview
private fun ContactsSectionNarrowPreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                ContactsSection(myProfile.contacts, isWideScreen = false, snackbarHostState = remember { SnackbarHostState() })
            }
        }
    }
}
