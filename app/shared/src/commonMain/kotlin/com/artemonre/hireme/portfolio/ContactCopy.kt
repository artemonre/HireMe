package com.artemonre.hireme.portfolio

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.unit.dp

internal expect fun Modifier.copyOnSecondaryClick(onCopy: () -> Unit): Modifier

@Composable
internal expect fun ContactCopyButton(onCopy: () -> Unit)

internal expect fun plainTextClipEntry(text: String): ClipEntry

// Hand-drawn stand-in for a "copy" icon (two overlapping rounded-rect outlines) — the project
// only depends on material-icons-core, which doesn't include ContentCopy; the full icon set
// (material-icons-extended) is a much larger dependency just for this one glyph.
@Composable
internal fun CopyIcon(modifier: Modifier = Modifier, tint: Color = LocalContentColor.current) {
    Canvas(modifier = modifier.size(18.dp)) {
        val strokeWidthPx = 1.5.dp.toPx()
        val corner = CornerRadius(2.dp.toPx())
        val squareSize = Size(size.width * 0.7f, size.height * 0.7f)
        drawRoundRect(
            color = tint,
            topLeft = Offset(size.width - squareSize.width, 0f),
            size = squareSize,
            cornerRadius = corner,
            style = Stroke(width = strokeWidthPx),
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(0f, size.height - squareSize.height),
            size = squareSize,
            cornerRadius = corner,
            style = Stroke(width = strokeWidthPx),
        )
    }
}
