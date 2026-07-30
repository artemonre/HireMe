package com.artemonre.hireme.portfolio

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.onClick
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.unit.dp
import java.awt.datatransfer.StringSelection

@OptIn(ExperimentalFoundationApi::class)
internal actual fun Modifier.copyOnSecondaryClick(onCopy: () -> Unit): Modifier =
    onClick(matcher = PointerMatcher.mouse(PointerButton.Secondary)) { onCopy() }

@Composable
internal actual fun ContactCopyButton(onCopy: () -> Unit) {
    // IconButton's default 48dp accessibility touch target (meant for imprecise touch input)
    // makes every contact row that tall even though this glyph is 12dp — needlessly bloating
    // list height on this pointer-driven, mouse-precise platform. Opt out of it here rather than
    // narrowing LocalMinimumInteractiveComponentSize globally, which would also shrink unrelated
    // buttons elsewhere in the app.
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        IconButton(onClick = onCopy, modifier = Modifier.size(24.dp)) {
            CopyIcon()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
internal actual fun plainTextClipEntry(text: String): ClipEntry = ClipEntry(StringSelection(text))
