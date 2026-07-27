package com.artemonre.hireme.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artemonre.hireme.theme.HireMeTheme

// A small "board game chip" matching the shape the app's chips already use (AssistChip's default
// small-corner shape), styled the same way as BoardgameCardVertical: a darker side face peeks out
// past the front face's bottom-left edge to suggest physical thickness, a drop shadow (attached to
// that back layer, since it's the surface actually touching the table) adds ambient depth, and a
// diagonal light-to-dark gradient across the front face simulates it catching light.
private val BoardgameChipCornerRadius = 8.dp
private val BoardgameChipShape = RoundedCornerShape(BoardgameChipCornerRadius)
private val BoardgameChipElevation = 3.dp
private val BoardgameChipExtrusionOffset = 1.dp
private val BoardgameChipHorizontalPadding = 8.dp
private val BoardgameChipVerticalPadding = 6.dp

@Composable
fun BoardgameChip(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    val sideColor = lerp(backgroundColor, Color.Black, 0.35f)
    val borderColor = lerp(backgroundColor, Color.Black, 0.2f)

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = -BoardgameChipExtrusionOffset, y = BoardgameChipExtrusionOffset)
                .shadow(elevation = BoardgameChipElevation, shape = BoardgameChipShape)
                .clip(BoardgameChipShape)
                .background(sideColor),
        )

        Box(
            modifier = Modifier
                .clip(BoardgameChipShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            lerp(backgroundColor, Color.White, 0.16f),
                            backgroundColor,
                            lerp(backgroundColor, Color.Black, 0.16f),
                        ),
                    ),
                )
                .border(BorderStroke(1.dp, borderColor), BoardgameChipShape)
                .padding(horizontal = BoardgameChipHorizontalPadding, vertical = BoardgameChipVerticalPadding),
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(color = textColor),
            )
        }
    }
}

@Composable
@Preview
private fun BoardgameChipPreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(modifier = Modifier.padding(16.dp)) {
                BoardgameChip(
                    text = "Kotlin",
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                    textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Spacer(Modifier.width(8.dp))
                BoardgameChip(
                    text = "Compose",
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}
