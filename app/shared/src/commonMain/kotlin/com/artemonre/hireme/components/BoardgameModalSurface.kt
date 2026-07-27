package com.artemonre.hireme.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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

// Modal chrome styled like the app's other "board game" pieces (BoardgameChip, BoardgameCardVertical):
// a darker side face peeks out from behind the front face to suggest physical thickness, with a
// drop shadow and a diagonal light-to-dark gradient across the front completing the effect.
//
// Those other pieces get the peek by offsetting the side face *outside* the front face's own
// bounds, relying on an unclipped ancestor to let it show. That trick doesn't work here: both hosts
// this is used from (ModalBottomSheet's Surface, Dialog's Surface) clip their content to their own
// bounds, so an offset child would just be cut off. Instead, the front face is inset from the shared
// box by the extrusion offset on whichever edges recede, leaving the darker side layer — sized to
// the full, uninset box — peeking out of the freed-up space rather than outside the box altogether.
enum class BoardgameModalExtrusionEdges { Left, LeftAndBottom }

private val BoardgameModalElevation = 8.dp
private val BoardgameModalExtrusionOffset = 3.dp

@Composable
fun BoardgameModalSurface(
    shape: Shape,
    backgroundColor: Color,
    edges: BoardgameModalExtrusionEdges,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val sideColor = lerp(backgroundColor, Color.Black, 0.35f)
    val borderColor = lerp(backgroundColor, Color.Black, 0.2f)

    val frontInsetModifier = when (edges) {
        BoardgameModalExtrusionEdges.Left ->
            Modifier.padding(start = BoardgameModalExtrusionOffset)
        BoardgameModalExtrusionEdges.LeftAndBottom ->
            Modifier.padding(start = BoardgameModalExtrusionOffset, bottom = BoardgameModalExtrusionOffset)
    }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .shadow(elevation = BoardgameModalElevation, shape = shape)
                .clip(shape)
                .background(sideColor),
        )

        Box(
            modifier = frontInsetModifier
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
private fun BoardgameModalSurfaceLeftPreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            BoardgameModalSurface(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                backgroundColor = MaterialTheme.colorScheme.surface,
                edges = BoardgameModalExtrusionEdges.Left,
                modifier = Modifier.padding(16.dp),
            ) {
                Text(text = "Bottom sheet content", modifier = Modifier.padding(24.dp))
            }
        }
    }
}

@Composable
@Preview
private fun BoardgameModalSurfaceLeftAndBottomPreview() {
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            BoardgameModalSurface(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colorScheme.surface,
                edges = BoardgameModalExtrusionEdges.LeftAndBottom,
                modifier = Modifier.padding(16.dp),
            ) {
                Text(text = "Dialog content", modifier = Modifier.padding(24.dp))
            }
        }
    }
}
