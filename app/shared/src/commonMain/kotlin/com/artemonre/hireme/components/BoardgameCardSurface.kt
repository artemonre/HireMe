package com.artemonre.hireme.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artemonre.hireme.theme.HireMeTheme

// The same "board game" volume effect as BoardgameChip/BoardgameCardVertical — a darker side face
// peeking out past the front face's bottom-left edge, a drop shadow on that back layer, and a
// diagonal light-to-dark gradient across the front — generalized to arbitrary content, for cards
// like Experience/Projects entries that don't share either component's fixed internal layout.
private val BoardgameCardSurfaceElevation = 6.dp
private val BoardgameCardSurfaceExtrusionOffset = 2.dp

@Composable
fun BoardgameCardSurface(
    shape: Shape,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val sideColor = lerp(backgroundColor, Color.Black, 0.35f)
    val borderColor = lerp(backgroundColor, Color.Black, 0.2f)

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = -BoardgameCardSurfaceExtrusionOffset, y = BoardgameCardSurfaceExtrusionOffset)
                .shadow(elevation = BoardgameCardSurfaceElevation, shape = shape)
                .clip(shape)
                .background(sideColor),
        )

        // Deliberately not matchParentSize — unlike BoardgameCardVertical/Chip (which are given a
        // fixed aspect ratio or size by their caller), this front face's wrap-content size is what
        // determines the outer Box's size, which the side face's matchParentSize then follows.
        Box(
            modifier = Modifier
                .clip(shape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            lerp(backgroundColor, Color.White, 0.16f),
                            backgroundColor,
                            lerp(backgroundColor, Color.Black, 0.16f),
                        ),
                    ),
                )
                .border(BorderStroke(1.dp, borderColor), shape),
        ) {
            content()
        }
    }
}

@Composable
@Preview
private fun BoardgameCardSurfacePreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            BoardgameCardSurface(
                shape = RoundedCornerShape(12.dp),
                backgroundColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(16.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Title", color = MaterialTheme.colorScheme.onSecondary)
                    Text(text = "Subtitle", color = MaterialTheme.colorScheme.onSecondary)
                }
            }
        }
    }
}
