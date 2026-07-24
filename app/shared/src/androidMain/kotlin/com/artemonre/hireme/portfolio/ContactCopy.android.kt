package com.artemonre.hireme.portfolio

import android.content.ClipData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry

internal actual fun Modifier.copyOnSecondaryClick(onCopy: () -> Unit): Modifier = this

@Composable
internal actual fun ContactCopyButton(onCopy: () -> Unit) {
}

internal actual fun plainTextClipEntry(text: String): ClipEntry =
    ClipEntry(ClipData.newPlainText("Copied text", text))
